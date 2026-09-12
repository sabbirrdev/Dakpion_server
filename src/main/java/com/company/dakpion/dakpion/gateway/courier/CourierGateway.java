package com.company.dakpion.dakpion.gateway.courier;

import com.company.dakpion.dakpion.dto.AdminCourierBookingRequestDto;
import com.company.dakpion.dakpion.dto.AdminCourierBookingResponseDto;
import com.company.dakpion.dakpion.entity.DakpionLetterEntity;

public interface CourierGateway {
    String getProviderName();
    AdminCourierBookingResponseDto bookCourier(DakpionLetterEntity letter, AdminCourierBookingRequestDto request);
    String trackStatus(String trackingId);
}
