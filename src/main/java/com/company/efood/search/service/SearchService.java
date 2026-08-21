package com.company.efood.search.service;

import com.company.efood.catalog.entity.CatalogProduct;
import com.company.efood.catalog.entity.ShopInventoryItem;
import com.company.efood.catalog.repository.CatalogProductRepo;
import com.company.efood.catalog.repository.ShopInventoryItemRepo;
import com.company.efood.search.dto.CatalogSearchResult;
import com.company.efood.search.dto.FoodSearchResult;
import com.company.efood.search.dto.SearchResultDto;
import com.company.efood.search.dto.ShopInventoryResult;
import com.company.efood.sys.entity.MenuItem;
import com.company.efood.sys.repository.MenuItemRepo;
import com.company.efood.sys.repository.ShopRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final CatalogProductRepo catalogProductRepo;
    private final ShopInventoryItemRepo inventoryRepo;
    private final MenuItemRepo menuItemRepo;
    private final ShopRepo shopRepo;

    public SearchResultDto search(String query, Long zoneId, String type) {
        SearchResultDto result = new SearchResultDto();
        result.setCatalogResults(new ArrayList<>());
        result.setFoodResults(new ArrayList<>());

        if (query == null || query.trim().isEmpty()) {
            return result;
        }

        boolean searchCatalog = "ALL".equalsIgnoreCase(type) || "GROCERY".equalsIgnoreCase(type) || "MEDICINE".equalsIgnoreCase(type);
        boolean searchFood = "ALL".equalsIgnoreCase(type) || "FOOD".equalsIgnoreCase(type);

        if (searchCatalog) {
            List<CatalogProduct> products = catalogProductRepo.searchByNameOrBrand(query);
            for (CatalogProduct p : products) {
                // optionally filter by type if type is not ALL
                if (!"ALL".equalsIgnoreCase(type) && !type.equalsIgnoreCase(p.getProductType())) {
                    continue;
                }
                
                CatalogSearchResult csr = new CatalogSearchResult();
                csr.setCatalogProductId(p.getId());
                csr.setProductName(p.getProductName());
                csr.setProductNameBn(p.getProductNameBn());
                csr.setBrand(p.getBrand());
                csr.setUnit(p.getUnit());
                csr.setImageUrl(p.getImageUrl());
                csr.setProductType(p.getProductType());

                List<ShopInventoryItem> items = inventoryRepo.findByCatalogProductIdAndIsActiveTrue(p.getId());
                List<ShopInventoryResult> inventoryResults = items.stream().map(item -> {
                    ShopInventoryResult sir = new ShopInventoryResult();
                    sir.setInventoryItemId(item.getId());
                    sir.setShopId(item.getShop().getId());
                    sir.setShopName(item.getShop().getShopName());
                    sir.setSellingPrice(item.getSellingPrice());
                    sir.setDiscountPrice(item.getDiscountPrice());
                    sir.setAvailableQty(item.getAvailableQty());
                    sir.setInStock(item.getAvailableQty() != null && item.getAvailableQty() > 0);
                    return sir;
                }).collect(Collectors.toList());

                csr.setAvailableAtShops(inventoryResults);
                result.getCatalogResults().add(csr);
            }
        }

        if (searchFood) {
            List<MenuItem> menuItems = menuItemRepo.searchActiveByName(query);
            for (MenuItem m : menuItems) {
                FoodSearchResult fsr = new FoodSearchResult();
                fsr.setMenuItemId(m.getId());
                fsr.setItemName(m.getName());
                fsr.setDescription(m.getBanglaName());
                // fsr.setPrice(m.getPrice()); // no price in MenuItem in existing schema?
                fsr.setImageUrl(m.getMenuIcon());
                
                // There is no shop directly on MenuItem, maybe we need to skip shop part if missing
                result.getFoodResults().add(fsr);
            }
        }

        result.setTotalResults(result.getCatalogResults().size() + result.getFoodResults().size());
        return result;
    }
}
