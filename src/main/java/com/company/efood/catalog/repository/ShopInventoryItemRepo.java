package com.company.efood.catalog.repository;

import com.company.efood.catalog.entity.ShopInventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopInventoryItemRepo extends JpaRepository<ShopInventoryItem, Long> {
    java.util.List<ShopInventoryItem> findByCatalogProductIdAndIsActiveTrue(Long catalogProductId);
}
