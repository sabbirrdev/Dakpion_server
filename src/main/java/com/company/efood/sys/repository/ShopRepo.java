package com.company.efood.sys.repository;

import com.company.efood.sys.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepo extends JpaRepository<Shop,Long> {
    Optional<Shop> findShopBySellerId(Long sellerId);
}
