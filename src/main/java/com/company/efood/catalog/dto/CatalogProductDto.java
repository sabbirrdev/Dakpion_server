package com.company.efood.catalog.dto;

import com.company.efood.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CatalogProductDto extends BaseDto {
    private String productName;
    private String productType = "GENERAL";
    private String serviceType;
    private String productNameBn;
    private String sku;
    private String description;
    private BigDecimal defaultPrice;
    private BigDecimal defaultDiscountPrice;
    private String unit;
    private String brand;
    private Long categoryId;
    private String categoryName;
    private Boolean isActiveForAllShops;
    private Boolean isDigital;
    private String imageUrl;
    private List<String> tags;
}
