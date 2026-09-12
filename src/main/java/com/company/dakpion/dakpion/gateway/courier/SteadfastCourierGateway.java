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
public class SteadfastCourierGateway implements CourierGateway {

    @Override
    public String getProviderName() {
        return "STEADFAST";
    }

    @Override
    public AdminCourierBookingResponseDto bookCourier(DakpionLetterEntity letter, AdminCourierBookingRequestDto request) {
        String bookingId = "STDF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String trackingCode = "STDF-TRK-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        log.info("[SteadfastCourier] Booked consignment for letter ID: {}, tracking: {}", letter.getId(), trackingCode);

        return AdminCourierBookingResponseDto.builder()
                .letterId(letter.getId().toString())
                .courierProvider("STEADFAST")
                .bookingId(bookingId)
                .trackingCode(trackingCode)
                .status("ACCEPTED")
                .trackingUrl("https://steadfast.com.bd/t/" + trackingCode)
                .estimatedDeliveryDate(LocalDate.now().plusDays(3).toString())
                .build();
    }

    @Override
    public String trackStatus(String trackingId) {
        return "IN_TRANSIT";
    }
}
