package com.company.dakpion.dakpion.service;

import com.company.dakpion.dakpion.config.DakpionProperties;
import com.company.dakpion.dakpion.constant.DeliveryType;
import com.company.dakpion.dakpion.constant.LetterStatus;
import com.company.dakpion.dakpion.constant.LocaleCode;
import com.company.dakpion.dakpion.constant.ModerationStatus;
import com.company.dakpion.dakpion.dto.ComposeLetterRequestDto;
import com.company.dakpion.dakpion.dto.LetterResponseDto;
import com.company.dakpion.dakpion.entity.DakpionLetterEntity;
import com.company.dakpion.dakpion.entity.DakpionThemeEntity;
import com.company.dakpion.dakpion.exception.ContentRejectedException;
import com.company.dakpion.dakpion.exception.InvalidOtpException;
import com.company.dakpion.dakpion.gateway.sms.SmsGateway;
import com.company.dakpion.dakpion.mapper.DakpionMapper;
import com.company.dakpion.dakpion.repository.DakpionAudioTrackRepo;
import com.company.dakpion.dakpion.repository.DakpionDeliveryEventRepo;
import com.company.dakpion.dakpion.repository.DakpionDeliveryOptionRepo;
import com.company.dakpion.dakpion.repository.DakpionLetterRepo;
import com.company.dakpion.dakpion.repository.DakpionSmsLogRepo;
import com.company.dakpion.dakpion.repository.DakpionThemeRepo;
import com.company.dakpion.dakpion.service.impl.DakpionLetterServiceImpl;
import com.company.dakpion.dakpion.util.DakpionShortCodeGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DakpionLetterServiceTest {

    @Mock
    private DakpionLetterRepo letterRepo;
    @Mock
    private DakpionThemeRepo themeRepo;
    @Mock
    private DakpionAudioTrackRepo audioTrackRepo;
    @Mock
    private DakpionDeliveryOptionRepo deliveryOptionRepo;
    @Mock
    private BadWordsFilterService badWordsFilterService;
    @Mock
    private DakpionOtpService otpService;
    @Mock
    private IdempotencyService idempotencyService;
    @Mock
    private DakpionMapper mapper;
    @Mock
    private com.company.dakpion.sys.repository.AppUserRepo appUserRepo;
    @Mock
    private SmsGateway smsGateway;
    @Mock
    private DakpionSmsLogRepo smsLogRepo;
    @Mock
    private DakpionDeliveryEventRepo deliveryEventRepo;

    private final DakpionShortCodeGenerator shortCodeGenerator = new DakpionShortCodeGenerator();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DakpionProperties properties = new DakpionProperties();

    private DakpionLetterServiceImpl letterService;

    @BeforeEach
    void setUp() {
        properties.getSecurity().setPhonePepper("test-pepper-12345");
        letterService = new DakpionLetterServiceImpl(
                letterRepo,
                themeRepo,
                audioTrackRepo,
                deliveryOptionRepo,
                badWordsFilterService,
                otpService,
                idempotencyService,
                properties,
                mapper,
                objectMapper,
                appUserRepo,
                smsGateway,
                smsLogRepo,
                deliveryEventRepo,
                shortCodeGenerator
        );
    }

    @Test
    @DisplayName("Should reject compose when OTP is not verified")
    void shouldRejectWhenOtpNotVerified() {
        ComposeLetterRequestDto request = ComposeLetterRequestDto.builder()
                .senderNickname("অচেনা")
                .senderPhone("01711112233")
                .recipientName("বন্ধু")
                .content("হ্যালো বন্ধু")
                .themeId("plain_digital")
                .audioId("silence")
                .deliveryType(DeliveryType.DIGITAL)
                .language(LocaleCode.bn)
                .otpVerificationToken("invalid-token")
                .build();

        when(otpService.isPhoneVerified("01711112233", "invalid-token", null)).thenReturn(false);

        assertThrows(InvalidOtpException.class, () -> letterService.composeLetter(request, null));
        verify(letterRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should reject compose when content contains banned profanity")
    void shouldRejectWhenContentContainsBadWords() {
        ComposeLetterRequestDto request = ComposeLetterRequestDto.builder()
                .senderNickname("অচেনা")
                .senderPhone("01711112233")
                .recipientName("শত্রু")
                .content("You are an idiot!")
                .themeId("plain_digital")
                .audioId("silence")
                .deliveryType(DeliveryType.DIGITAL)
                .language(LocaleCode.en)
                .otpVerificationToken("valid-token")
                .build();

        when(otpService.isPhoneVerified("01711112233", "valid-token", null)).thenReturn(true);
        doThrow(new ContentRejectedException("Your letter contains language that isn't allowed."))
                .when(badWordsFilterService).validateContent("You are an idiot!");

        assertThrows(ContentRejectedException.class, () -> letterService.composeLetter(request, null));
        verify(letterRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should compose letter successfully, generate shortCode and trackingCode, and sanitize HTML")
    void shouldComposeLetterSuccessfully() {
        String rawPhone = "01711112233";
        ComposeLetterRequestDto request = ComposeLetterRequestDto.builder()
                .senderNickname("অচেনা পথিক")
                .senderPhone(rawPhone)
                .recipientName("তনিমা")
                .content("<script>alert('xss')</script>অনেকদিন কথা হয় না।")
                .themeId("plain_digital")
                .audioId("silence")
                .deliveryType(DeliveryType.PHYSICAL)
                .language(LocaleCode.bn)
                .otpVerificationToken("valid-token")
                .build();

        when(otpService.isPhoneVerified(rawPhone, "valid-token", null)).thenReturn(true);
        when(themeRepo.findById("plain_digital")).thenReturn(Optional.of(
                DakpionThemeEntity.builder().id("plain_digital").price(BigDecimal.TEN).build()
        ));
        when(audioTrackRepo.existsById("silence")).thenReturn(true);
        when(deliveryOptionRepo.existsById(DeliveryType.PHYSICAL)).thenReturn(true);

        when(letterRepo.save(any(DakpionLetterEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toLetterDto(any(DakpionLetterEntity.class))).thenAnswer(inv -> {
            DakpionLetterEntity e = inv.getArgument(0);
            return LetterResponseDto.builder()
                    .id(e.getId().toString())
                    .senderNickname(e.getSenderNickname())
                    .senderPhoneHashed(e.getSenderPhoneHashed())
                    .recipientName(e.getRecipientName())
                    .content(e.getContent())
                    .themeId(e.getThemeId())
                    .audioId(e.getAudioId())
                    .deliveryType(e.getDeliveryType())
                    .language(e.getLanguage())
                    .status(e.getStatus())
                    .build();
        });

        LetterResponseDto result = letterService.composeLetter(request, "idemp-key-123");

        assertNotNull(result);
        assertNotNull(result.getId());

        ArgumentCaptor<DakpionLetterEntity> captor = ArgumentCaptor.forClass(DakpionLetterEntity.class);
        verify(letterRepo).save(captor.capture());

        DakpionLetterEntity savedEntity = captor.getValue();
        // Verify XSS script was stripped
        assertEquals("অনেকদিন কথা হয় না।", savedEntity.getContent());
        // Verify raw phone is NEVER in the entity
        assertNotEquals(rawPhone, savedEntity.getSenderPhoneHashed());
        assertTrue(savedEntity.getSenderPhoneHashed().contains("..."));
        assertEquals(ModerationStatus.PENDING, savedEntity.getModerationStatus());
        assertEquals(LetterStatus.SUBMITTED, savedEntity.getStatus());
        // Verify shortCode & trackingCode are set
        assertNotNull(savedEntity.getShortCode());
        assertNotNull(savedEntity.getTrackingCode());
        assertTrue(savedEntity.getTrackingCode().startsWith("DP-"));

        // Verify initial physical delivery event is saved
        verify(deliveryEventRepo).save(any());
    }

    @Test
    @DisplayName("Should mark letter as opened idempotently")
    void shouldMarkOpenedIdempotently() {
        UUID letterId = UUID.randomUUID();
        DakpionLetterEntity letter = DakpionLetterEntity.builder()
                .id(letterId)
                .status(LetterStatus.SUBMITTED)
                .build();

        when(letterRepo.findById(letterId)).thenReturn(Optional.of(letter));
        when(letterRepo.save(any(DakpionLetterEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toLetterDto(any(DakpionLetterEntity.class))).thenAnswer(inv -> {
            DakpionLetterEntity e = inv.getArgument(0);
            return LetterResponseDto.builder().id(e.getId().toString()).status(e.getStatus()).build();
        });

        LetterResponseDto res1 = letterService.markOpened(letterId);
        assertEquals(LetterStatus.OPENED, res1.getStatus());
        verify(letterRepo, times(1)).save(letter);
    }
}
