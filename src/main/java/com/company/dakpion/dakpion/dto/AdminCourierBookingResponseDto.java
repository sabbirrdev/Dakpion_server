package com.company.dakpion.dakpion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminCourierBookingResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String letterId;
    private String courierProvider;
    private String bookingId;
    private String trackingCode;
    private String status;
    private String trackingUrl;
    private String estimatedDeliveryDate;
}
