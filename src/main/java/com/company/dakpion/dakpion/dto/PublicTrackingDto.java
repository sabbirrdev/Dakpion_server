package com.company.dakpion.dakpion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Public-safe tracking DTO. Contains ZERO PII.
 * No names, phones, addresses, or letter content.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicTrackingDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String trackingCode;
    private String currentStatus;
    private LocalDateTime estimatedDelivery;
    private List<DeliveryEventDto> events;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryEventDto implements Serializable {
        private static final long serialVersionUID = 1L;

        private String status;
        private String note;
        private LocalDateTime occurredAt;
    }
}
