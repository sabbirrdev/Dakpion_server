package com.company.efood.catalog.dto;

import com.company.efood.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShopInventoryItemDto extends BaseDto {
    private Long catalogProductId;
    private Long shopId;
    private Long branchId;
    private BigDecimal sellingPrice;
    private BigDecimal discountPrice;
    private Integer availableQty;
    private Integer minQty;
    private Boolean isActive;
    private Boolean isDefaultForShop;
    private String customName;
    private String customDescription;
    private String catalogProductName;
}
