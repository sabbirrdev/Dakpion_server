package com.company.dakpion.dakpion.service.impl;

import com.company.dakpion.config.CurrentUserContext;
import com.company.dakpion.dakpion.config.DakpionProperties;
import com.company.dakpion.dakpion.constant.*;
import com.company.dakpion.dakpion.dto.ComposeLetterRequestDto;
import com.company.dakpion.dakpion.dto.LetterResponseDto;
import com.company.dakpion.dakpion.entity.*;
import com.company.dakpion.dakpion.exception.InvalidOtpException;
import com.company.dakpion.dakpion.exception.ResourceNotFoundException;
import com.company.dakpion.dakpion.gateway.sms.SmsGateway;
import com.company.dakpion.dakpion.gateway.sms.SmsNetBdGateway;
import com.company.dakpion.dakpion.gateway.sms.dto.SmsSendResult;
import com.company.dakpion.dakpion.gateway.sms.exception.SmsGatewayException;
import com.company.dakpion.dakpion.mapper.DakpionMapper;
import com.company.dakpion.dakpion.repository.*;
import com.company.dakpion.dakpion.service.BadWordsFilterService;
import com.company.dakpion.dakpion.service.DakpionLetterService;
import com.company.dakpion.dakpion.service.DakpionOtpService;
import com.company.dakpion.dakpion.service.IdempotencyService;
import com.company.dakpion.dakpion.util.DakpionShortCodeGenerator;
import com.company.dakpion.sys.entity.AppUser;
import com.company.dakpion.sys.utils.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DakpionLetterServiceImpl implements DakpionLetterService {

    private static final String APP_BASE_URL = "https://dakpion.com";

    private final DakpionLetterRepo letterRepo;
    private final DakpionThemeRepo themeRepo;
    private final DakpionAudioTrackRepo audioTrackRepo;
    private final DakpionDeliveryOptionRepo deliveryOptionRepo;
    private final BadWordsFilterService badWordsFilterService;
    private final DakpionOtpService otpService;
    private final IdempotencyService idempotencyService;
    private final DakpionProperties properties;
    private final DakpionMapper mapper;
    private final ObjectMapper objectMapper;
    private final com.company.dakpion.sys.repository.AppUserRepo appUserRepo;
    private final SmsGateway smsGateway;
    private final DakpionSmsLogRepo smsLogRepo;
    private final DakpionDeliveryEventRepo deliveryEventRepo;
    private final DakpionShortCodeGenerator shortCodeGenerator;

    // -------------------------------------------------------------------------
    // Compose Letter
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public LetterResponseDto composeLetter(ComposeLetterRequestDto request, String idempotencyKey) {
        // 1. Idempotency check
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<LetterResponseDto> cached = idempotencyService.getCachedResponse(idempotencyKey);
            if (cached.isPresent()) {
                log.info("[Compose] Returning cached idempotency response for key: {}", idempotencyKey);
                return cached.get();
            }
            Optional<DakpionLetterEntity> existing = letterRepo.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                LetterResponseDto dto = mapper.toLetterDto(existing.get());
                idempotencyService.saveResponse(idempotencyKey, dto);
                return dto;
            }
        }

        // 2. Phone / OTP verification
        // NOTE: currently requires a prior logged-in+verified account — guest compose
        // is broken. See separate note; leaving structure as-is for this fix, but this
        // needs an inline-OTP fallback branch before guests can send anything.
        boolean isVerified = false;
        Long currentUserId = CurrentUserContext.getUserId();
        if (currentUserId != null) {
            AppUser currentUser = appUserRepo.findById(currentUserId).orElseThrow();
            if (Boolean.TRUE.equals(currentUser.getPhoneVerified()) && currentUser.getPhone() != null && !currentUser.getPhone().isBlank()) {
                isVerified = true;
                request.setSenderPhone(currentUser.getPhone());
            }
        }
        if (!isVerified) {
            throw new InvalidOtpException("Sender phone has not been verified with OTP.");
        }

        // 3. Content moderation — run against PLAIN TEXT, not raw HTML, so tags can't
        // be used to dodge the filter (e.g. "id<span>iot</span>")
        String plainTextForModeration = Jsoup.parse(request.getContent()).text();
        badWordsFilterService.validateContent(plainTextForModeration);

        // 4. XSS sanitization
        Safelist safelist = Safelist.relaxed()
                .addTags("span", "u", "s", "hr")
                .addAttributes("span", "style", "class")
                .addAttributes("p", "style", "class")
                .addAttributes("h1", "style", "class").addAttributes("h2", "style", "class")
                .addAttributes("h3", "style", "class").addAttributes("h4", "style", "class");
        String sanitizedContent = Jsoup.clean(request.getContent(), safelist);

        // 5. Hash phone
        String phoneHashed = SecurityUtils.hashPhoneWithPepper(request.getSenderPhone(), properties.getSecurity().getPhonePepper());

        // 6. Validate references AND load the actual priced entities — never trust
        // client-supplied prices, always price from the DB record the ID points at
        DakpionThemeEntity theme = themeRepo.findById(request.getThemeId())
                .orElseThrow(() -> new ResourceNotFoundException("Theme '" + request.getThemeId() + "' not found."));
        DakpionAudioTrackEntity audioTrack = audioTrackRepo.findById(request.getAudioId())
                .orElseThrow(() -> new ResourceNotFoundException("Audio track '" + request.getAudioId() + "' not found."));
        DakpionDeliveryOptionEntity deliveryOption = deliveryOptionRepo.findById(request.getDeliveryType())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery option '" + request.getDeliveryType() + "' not found."));

        // 6b. Server-side delivery-specific validation (never trust the frontend
        // to have enforced this — it must be re-checked here)
        boolean needsRecipientPhone = request.getDeliveryType() == DeliveryType.SMS_SPEED_POST
                || request.getDeliveryType() == DeliveryType.PHYSICAL;
        if (needsRecipientPhone && (request.getRecipientPhone() == null || request.getRecipientPhone().isBlank())) {
            throw new ResourceNotFoundException("Recipient phone is required for this delivery method.");
        }
        if (request.getDeliveryType() == DeliveryType.PHYSICAL
                && (request.getShippingAddress() == null
                || !StringUtils.hasText(request.getShippingAddress().getFullAddress()))) {
            throw new ResourceNotFoundException("Delivery address is required for physical delivery.");
        }

        // 7. Compute pricing — this is the actual fix: total is the sum of whatever
        // the theme, audio track, and delivery option each cost, not a hardcoded
        // guess based only on delivery type
        BigDecimal themeAmount = (theme.getTier() == ThemeTier.PREMIUM && theme.getPrice() != null) ? theme.getPrice() : BigDecimal.ZERO;
        BigDecimal audioAmount = (Boolean.TRUE.equals(audioTrack.getIsPremium()) && audioTrack.getPrice() != null) ? audioTrack.getPrice() : BigDecimal.ZERO;
        BigDecimal deliveryAmount = deliveryOption.getPrice() != null ? deliveryOption.getPrice() : BigDecimal.ZERO;
        BigDecimal totalAmount = themeAmount.add(audioAmount).add(deliveryAmount);

        // 8. Payment status — driven by the actual computed total, not delivery type
        PaymentStatus paymentStatus = totalAmount.compareTo(BigDecimal.ZERO) == 0 ? PaymentStatus.NOT_APPLICABLE : PaymentStatus.UNPAID;

        // 9. Shipping address
        String shippingAddressJson = null;
        if (request.getShippingAddress() != null) {
            try { shippingAddressJson = objectMapper.writeValueAsString(request.getShippingAddress()); }
            catch (Exception e) { log.warn("[Compose] Failed serializing shipping address", e); }
        }

        // 10. Generate short code (always) and tracking code (physical only)
        String shortCode    = generateUniqueShortCode();
        String trackingCode = (request.getDeliveryType() == DeliveryType.PHYSICAL)
                ? generateUniqueTrackingCode()
                : null;

        // 11. Persist letter
        UUID letterId = UUID.randomUUID();
        DakpionLetterEntity entity = DakpionLetterEntity.builder()
                .id(letterId)
                .senderNickname(request.getSenderNickname().trim())
                .senderPhoneHashed(phoneHashed)
                .recipientName(request.getRecipientName().trim())
                .recipientPhone(request.getRecipientPhone() != null ? request.getRecipientPhone().trim() : null)
                .content(sanitizedContent)
                .themeId(request.getThemeId())
                .audioId(request.getAudioId())
                .deliveryType(request.getDeliveryType())
                .shippingAddress(shippingAddressJson)
                .themeAmount(themeAmount)
                .audioAmount(audioAmount)
                .deliveryAmount(deliveryAmount)
                .totalAmount(totalAmount)
                .currency("BDT")
                .paymentStatus(paymentStatus)
                .moderationStatus(ModerationStatus.PENDING)
                .status(LetterStatus.SUBMITTED)
                .language(request.getLanguage())
                .idempotencyKey(idempotencyKey)
                .shortCode(shortCode)
                .trackingCode(trackingCode)
                .build();

        DakpionLetterEntity saved = letterRepo.save(entity);
        log.info("[Compose] Letter {} created. delivery={} total={} {} shortCode={} trackingCode={}",
                saved.getId(), saved.getDeliveryType(), totalAmount, saved.getCurrency(), shortCode, trackingCode);

        // 12. Initial delivery event for PHYSICAL letters
        if (saved.getDeliveryType() == DeliveryType.PHYSICAL) {
            deliveryEventRepo.save(DakpionDeliveryEventEntity.builder()
                    .letterId(saved.getId())
                    .status(DeliveryEventStatus.SUBMITTED)
                    .note("Letter submitted by sender")
                    .occurredAt(LocalDateTime.now())
                    .createdBy("SYSTEM")
                    .build());
        }

        LetterResponseDto responseDto = mapper.toLetterDto(saved);
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            idempotencyService.saveResponse(idempotencyKey, responseDto);
        }
        return responseDto;
    }
    /**
     * Triggered when a SMS_SPEED_POST letter is approved and paid.
     * Sends an anonymous intrigue SMS to the recipient with the short link.
     * Fires asynchronously so any SMS failure does NOT roll back the approval.
     */
    @Async
    public void sendSpeedPostSmsAlert(DakpionLetterEntity letter) {
        if (letter.getRecipientPhone() == null || letter.getRecipientPhone().isBlank()) {
            log.warn("[SpeedPostSMS] No recipient phone on letter {}", letter.getId());
            return;
        }
        if (letter.getShortCode() == null || letter.getShortCode().isBlank()) {
            log.warn("[SpeedPostSMS] No short code on letter {}", letter.getId());
            return;
        }

        String shortLink = APP_BASE_URL + "/l/" + letter.getShortCode();
        String message   = buildSpeedPostSms(letter.getLanguage(), shortLink);
        String phone     = letter.getRecipientPhone();

        DakpionSmsLogEntity logEntry = DakpionSmsLogEntity.builder()
                .toNumber(phone)
                .purpose(SmsPurpose.SPEED_POST_ALERT)
                .messageContent(message)
                .status(SmsStatus.PENDING)
                .letterId(letter.getId())
                .createdAt(LocalDateTime.now())
                .build();

        try {
            if (smsGateway instanceof SmsNetBdGateway netBdGw) {
                SmsSendResult result = netBdGw.send(SmsNetBdGateway.normalizePhone(phone), message);
                logEntry.setRequestId(result.getRequestId());
                logEntry.setStatus(result.isSuccess() ? SmsStatus.SENT : SmsStatus.FAILED);
                if (!result.isSuccess()) {
                    logEntry.setErrorCode(result.getErrorCode());
                    logEntry.setErrorMessage(result.getErrorMessage());
                }
            } else {
                boolean sent = smsGateway.sendSms(phone, message);
                logEntry.setStatus(sent ? SmsStatus.SENT : SmsStatus.FAILED);
            }
            log.info("[SpeedPostSMS] SMS dispatched for letter {} to {}",
                    letter.getId(), SmsNetBdGateway.maskPhone(phone));
        } catch (SmsGatewayException sge) {
            log.error("[SpeedPostSMS] Gateway error for letter {}: [{}] {}",
                    letter.getId(), sge.getErrorCode(), sge.getMessage());
            logEntry.setStatus(SmsStatus.FAILED);
            logEntry.setErrorCode(sge.getErrorCode());
            logEntry.setErrorMessage(sge.getMessage());
        } catch (Exception e) {
            log.error("[SpeedPostSMS] Unexpected error for letter {}: {}", letter.getId(), e.getMessage());
            logEntry.setStatus(SmsStatus.FAILED);
            logEntry.setErrorMessage(e.getMessage());
        } finally {
            try { smsLogRepo.save(logEntry); }
            catch (Exception e) { log.warn("[SpeedPostSMS] Could not save SMS log: {}", e.getMessage()); }
        }
    }

    private String buildSpeedPostSms(LocaleCode locale, String shortLink) {
        if (locale == LocaleCode.bn) {
            return "ডাকপিওনে আপনার জন্য একটি চিঠি এসেছে। পড়তে ক্লিক করুন: " + shortLink;
        }
        return "Someone sent you a letter on DakPion. Read it here: " + shortLink;
    }

    // -------------------------------------------------------------------------
    // Get & Open
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public LetterResponseDto getLetterById(UUID id) {
        return mapper.toLetterDto(
                letterRepo.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("This letter could not be found."))
        );
    }

    @Override
    @Transactional
    public LetterResponseDto markOpened(UUID id) {
        DakpionLetterEntity entity = letterRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("This letter could not be found."));
        if (entity.getStatus() != LetterStatus.OPENED) {
            entity.setStatus(LetterStatus.OPENED);
            entity.setOpenedAt(LocalDateTime.now());
            entity = letterRepo.save(entity);
            log.info("[Letter] {} marked OPENED at {}", id, entity.getOpenedAt());
        }
        return mapper.toLetterDto(entity);
    }

    // -------------------------------------------------------------------------
    // Short code / tracking code generation (collision-safe)
    // -------------------------------------------------------------------------

    private String generateUniqueShortCode() {
        for (int i = 0; i < 10; i++) {
            String code = shortCodeGenerator.generateShortCode();
            if (!letterRepo.existsByShortCode(code)) return code;
        }
        throw new RuntimeException("Failed to generate unique short code after 10 attempts");
    }

    private String generateUniqueTrackingCode() {
        for (int i = 0; i < 10; i++) {
            String code = shortCodeGenerator.generateTrackingCode();
            if (!letterRepo.existsByTrackingCode(code)) return code;
        }
        throw new RuntimeException("Failed to generate unique tracking code after 10 attempts");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

}
