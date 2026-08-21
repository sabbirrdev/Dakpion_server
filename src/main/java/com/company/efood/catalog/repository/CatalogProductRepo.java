package com.company.efood.catalog.repository;

import com.company.efood.catalog.entity.CatalogProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogProductRepo extends JpaRepository<CatalogProduct, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT p FROM CatalogProduct p WHERE (LOWER(p.productName) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(p.brand) LIKE LOWER(CONCAT('%',:q,'%'))) AND p.active = true")
    java.util.List<CatalogProduct> searchByNameOrBrand(@org.springframework.data.repository.query.Param("q") String q);

    java.util.Optional<CatalogProduct> findByBarcode(String barcode);
    java.util.Optional<CatalogProduct> findBySku(String sku);
}
