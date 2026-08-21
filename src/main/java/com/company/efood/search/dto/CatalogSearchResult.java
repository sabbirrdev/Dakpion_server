package com.company.efood.search.dto;

import lombok.Data;
import java.util.List;

@Data
public class CatalogSearchResult {
    private Long catalogProductId;
    private String productName;
    private String productNameBn;
    private String brand;
    private String unit;
    private String imageUrl;
    private String productType; // GROCERY, MEDICINE
    private List<ShopInventoryResult> availableAtShops; // shops selling it
}
