package com.company.dakpion.dakpion.gateway.courier;

import com.company.dakpion.dakpion.config.DakpionProperties;
import com.company.dakpion.dakpion.dto.AdminCourierBookingRequestDto;
import com.company.dakpion.dakpion.dto.AdminCourierBookingResponseDto;
import com.company.dakpion.dakpion.entity.DakpionLetterEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PathaoCourierGateway implements CourierGateway {

    private final DakpionProperties properties;

    @Override
    public String getProviderName() {
        return "PATHAO";
    }

    @Override
    public AdminCourierBookingResponseDto bookCourier(DakpionLetterEntity letter, AdminCourierBookingRequestDto request) {
        String bookingId = "PTH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String trackingCode = "PTH-TRK-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        log.info("[PathaoCourier] Booked order for letter ID: {}, recipient: {}, tracking: {}",
                letter.getId(), letter.getRecipientName(), trackingCode);

        return AdminCourierBookingResponseDto.builder()
                .letterId(letter.getId().toString())
                .courierProvider("PATHAO")
                .bookingId(bookingId)
                .trackingCode(trackingCode)
                .status("ACCEPTED")
                .trackingUrl("https://merchant.pathao.com/tracking?consignment_id=" + trackingCode)
                .estimatedDeliveryDate(LocalDate.now().plusDays(2).toString())
                .build();
    }

    @Override
    public String trackStatus(String trackingId) {
        return "DISPATCHED";
    }
}
