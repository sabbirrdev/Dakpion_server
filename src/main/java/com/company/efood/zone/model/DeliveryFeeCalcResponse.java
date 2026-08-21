package com.company.efood.zone.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class DeliveryFeeCalcResponse {
    private Long zoneId;
    private String zoneName;
    private double distanceKm;
    private BigDecimal baseDeliveryFee;
    private BigDecimal extraCharge;
    private BigDecimal totalCharge;
}
