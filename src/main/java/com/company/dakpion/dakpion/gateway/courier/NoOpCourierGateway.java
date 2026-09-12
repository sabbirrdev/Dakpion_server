package com.company.dakpion.dakpion.gateway.courier;

import com.company.dakpion.dakpion.dto.AdminCourierBookingRequestDto;
import com.company.dakpion.dakpion.dto.AdminCourierBookingResponseDto;
import com.company.dakpion.dakpion.entity.DakpionLetterEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Component
public class NoOpCourierGateway implements CourierGateway {

    @Override
    public String getProviderName() {
        return "NOOP";
    }

    @Override
    public AdminCourierBookingResponseDto bookCourier(DakpionLetterEntity letter, AdminCourierBookingRequestDto request) {
        String bookingId = "BOOK-NOOP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String trackingCode = "TRK-NOOP-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        log.info("[Courier-NOOP] Simulated courier booking for letter {} to recipient {}. Tracking: {}",
                letter.getId(), letter.getRecipientName(), trackingCode);

        return AdminCourierBookingResponseDto.builder()
                .letterId(letter.getId().toString())
                .courierProvider("NOOP")
                .bookingId(bookingId)
                .trackingCode(trackingCode)
                .status("BOOKED")
                .trackingUrl("https://dakpion.app/track/" + trackingCode)
                .estimatedDeliveryDate(LocalDate.now().plusDays(3).toString())
                .build();
    }

    @Override
    public String trackStatus(String trackingId) {
        return "IN_TRANSIT";
    }
}
