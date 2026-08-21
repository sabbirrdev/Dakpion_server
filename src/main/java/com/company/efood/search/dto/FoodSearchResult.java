package com.company.efood.search.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FoodSearchResult {
    private Long menuItemId;
    private String itemName;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Long shopId;
    private String shopName;
    private String shopLogoUrl;
    private String shopType;
}
