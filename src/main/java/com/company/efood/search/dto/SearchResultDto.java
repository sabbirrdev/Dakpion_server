package com.company.efood.search.dto;

import lombok.Data;
import java.util.List;

@Data
public class SearchResultDto {
    private List<CatalogSearchResult> catalogResults; // Grocery/Medicine
    private List<FoodSearchResult> foodResults;       // Restaurant items
    private int totalResults;
}
