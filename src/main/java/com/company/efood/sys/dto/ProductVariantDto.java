package com.company.efood.sys.dto;
import com.company.efood.base.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductVariantDto extends BaseDto {
    private String variantName; // e.g., "Large", "Double Patty"
    private BigDecimal extraPrice;
}
