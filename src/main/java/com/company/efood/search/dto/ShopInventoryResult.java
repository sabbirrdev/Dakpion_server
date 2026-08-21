package com.company.efood.search.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ShopInventoryResult {
    private Long inventoryItemId;
    private Long shopId;
    private String shopName;
    private BigDecimal sellingPrice;
    private BigDecimal discountPrice;
    private Integer availableQty;
    private boolean inStock;
}
