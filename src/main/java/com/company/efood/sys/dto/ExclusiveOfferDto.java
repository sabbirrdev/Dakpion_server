package com.company.efood.sys.dto;

import com.company.efood.base.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ExclusiveOfferDto extends BaseDto {
    private String code;
    private String title;
    private String description;
    private String tag;
    private BigDecimal discountValue;
    private String discountType; // "PERCENTAGE" or "FIXED"
    private BigDecimal minOrderAmount;
    private String bannerUrl;
    private String colorHex;
    private String validTill;
    private LocalDateTime expiryDate;
    private Boolean isActive = true;
}
