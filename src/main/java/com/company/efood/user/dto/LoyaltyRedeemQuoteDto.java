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
public class LoyaltyRedeemQuoteDto {
    private Integer requestedPoints;
    private Integer eligiblePoints;
    private BigDecimal discountAmount;
    private BigDecimal maxAllowedDiscount;
    private BigDecimal newTotalAmount;
    private Boolean isValid;
    private String message;
}
