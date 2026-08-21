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
public class ProductAddonDto extends BaseDto {
    private String addonName; // e.g., "Extra Cheese"
    private BigDecimal price;
}
