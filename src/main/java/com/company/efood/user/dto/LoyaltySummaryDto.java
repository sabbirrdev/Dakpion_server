package com.company.efood.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltySummaryDto {
    private Integer currentPoints;
    private Integer lifetimePointsEarned;
    private BigDecimal monetaryValue;
    private Integer pointsPerHundredSpend;
    private BigDecimal currencyPerPoint;
    private Integer minPointsToRedeem;
    private Integer maxDiscountPercentage;
}
