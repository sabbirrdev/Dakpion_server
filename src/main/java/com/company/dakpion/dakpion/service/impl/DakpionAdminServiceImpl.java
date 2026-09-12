package com.company.dakpion.dakpion.service.impl;

import com.company.dakpion.dakpion.constant.DeliveryEventStatus;
import com.company.dakpion.dakpion.constant.DeliveryType;
import com.company.dakpion.dakpion.constant.LetterStatus;
import com.company.dakpion.dakpion.constant.ModerationStatus;
import com.company.dakpion.dakpion.dto.AdminCourierBookingRequestDto;
import com.company.dakpion.dakpion.dto.AdminCourierBookingResponseDto;
import com.company.dakpion.dakpion.dto.AdminModerationRequestDto;
import com.company.dakpion.dakpion.dto.LetterResponseDto;
import com.company.dakpion.dakpion.entity.DakpionDeliveryEventEntity;
import com.company.dakpion.dakpion.entity.DakpionLetterEntity;
import com.company.dakpion.dakpion.exception.DakpionException;
import com.company.dakpion.dakpion.exception.ResourceNotFoundException;
import com.company.dakpion.dakpion.gateway.courier.CourierGateway;
import com.company.dakpion.dakpion.gateway.pdf.PdfGenerationService;
import com.company.dakpion.dakpion.mapper.DakpionMapper;
import com.company.dakpion.dakpion.repository.DakpionDeliveryEventRepo;
import com.company.dakpion.dakpion.repository.DakpionLetterRepo;
import com.company.dakpion.dakpion.service.DakpionAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DakpionAdminServiceImpl implements DakpionAdminService {

    private final DakpionLetterRepo letterRepo;
    private final List<CourierGateway> courierGateways;
    private final PdfGenerationService pdfGenerationService;
    private final DakpionMapper mapper;
    private final DakpionDeliveryEventRepo deliveryEventRepo;
    private final ApplicationContext applicationContext;

    @Override
    @Transactional(readOnly = true)
    public Page<LetterResponseDto> getModerationQueue(ModerationStatus status, Pageable pageable) {
        Page<DakpionLetterEntity> page = (status != null)
                ? letterRepo.findAllByModerationStatus(status, pageable)
                : letterRepo.findAll(pageable);
        return page.map(mapper::toLetterDto);
    }

    @Override
    @Transactional
    public LetterResponseDto moderateLetter(UUID id, AdminModerationRequestDto request, Long adminUserId) {
        DakpionLetterEntity letter = letterRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Letter not found: " + id));

        letter.setModerationStatus(request.getDecision());
        letter.setModerationReason(request.getReason());
        letter.setModeratedBy(adminUserId);
        letter.setModeratedAt(LocalDateTime.now());

        if (request.getDecision() == ModerationStatus.APPROVED) {
            // Append moderation-approved delivery event for physical letters
            if (letter.getDeliveryType() == DeliveryType.PHYSICAL) {
                deliveryEventRepo.save(DakpionDeliveryEventEntity.builder()
                        .letterId(letter.getId())
                        .status(DeliveryEventStatus.MODERATION_APPROVED)
                        .note("Approved by admin #" + adminUserId)
                        .occurredAt(LocalDateTime.now())
                        .createdBy("ADMIN:" + adminUserId)
                        .build());
            }

            // Trigger Speed Post SMS asynchronously (must NOT block this transaction)
            if (letter.getDeliveryType() == DeliveryType.SMS_SPEED_POST) {
                DakpionLetterEntity finalLetter = letter;
                // Use proxy bean so @Async is respected
                applicationContext.getBean(DakpionLetterServiceImpl.class).sendSpeedPostSmsAlert(finalLetter);
            }

        } else if (request.getDecision() == ModerationStatus.REJECTED) {
            log.info("[Moderation] Letter {} REJECTED. Reason: {}", id, request.getReason());
            if (letter.getDeliveryType() == DeliveryType.PHYSICAL) {
                deliveryEventRepo.save(DakpionDeliveryEventEntity.builder()
                        .letterId(letter.getId())
                        .status(DeliveryEventStatus.MODERATION_REJECTED)
                        .note("Rejected by admin #" + adminUserId + ": " + request.getReason())
                        .occurredAt(LocalDateTime.now())
                        .createdBy("ADMIN:" + adminUserId)
                        .build());
            }
        }

        letter = letterRepo.save(letter);
        log.info("[Moderation] Letter {} moderated as {} by admin {}", id, request.getDecision(), adminUserId);
        return mapper.toLetterDto(letter);
    }

    @Override
    @Transactional
    public AdminCourierBookingResponseDto bookCourier(UUID id, AdminCourierBookingRequestDto request) {
        DakpionLetterEntity letter = letterRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Letter not found: " + id));

        if (letter.getDeliveryType() != DeliveryType.PHYSICAL) {
            throw new DakpionException("INVALID_DELIVERY_TYPE",
                    "Courier booking only applies to PHYSICAL delivery.", HttpStatus.BAD_REQUEST);
        }
        if (letter.getModerationStatus() != ModerationStatus.APPROVED) {
            throw new DakpionException("MODERATION_REQUIRED",
                    "Letter must be approved before booking courier.", HttpStatus.BAD_REQUEST);
        }

        CourierGateway gateway = findCourierGateway(request.getCourierProvider());
        AdminCourierBookingResponseDto booking = gateway.bookCourier(letter, request);

        letter.setCourierBookingId(booking.getBookingId());
        letter.setCourierTrackingId(booking.getTrackingCode());
        letter.setCourierStatus(booking.getStatus());
        letter.setStatus(LetterStatus.DELIVERED);
        letterRepo.save(letter);

        // Append delivery event
        deliveryEventRepo.save(DakpionDeliveryEventEntity.builder()
                .letterId(letter.getId())
                .status(DeliveryEventStatus.HANDED_TO_COURIER)
                .note("Booked with courier " + request.getCourierProvider() + ". Tracking: " + booking.getTrackingCode())
                .occurredAt(LocalDateTime.now())
                .createdBy("SYSTEM")
                .build());

        log.info("[Courier] Letter {} booked with {}. Tracking: {}", id, request.getCourierProvider(), booking.getTrackingCode());
        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] printVintagePdf(UUID id) {
        DakpionLetterEntity letter = letterRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Letter not found: " + id));
        return pdfGenerationService.generateVintageLetterPdf(letter);
    }

    private CourierGateway findCourierGateway(String provider) {
        if (provider == null || provider.isBlank()) return courierGateways.get(0);
        return courierGateways.stream()
                .filter(g -> g.getProviderName().equalsIgnoreCase(provider))
                .findFirst()
                .orElse(courierGateways.get(0));
    }
}
