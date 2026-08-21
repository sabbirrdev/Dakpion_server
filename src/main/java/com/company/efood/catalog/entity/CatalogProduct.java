package com.company.efood.catalog.entity;

import com.company.efood.base.BaseEntity;
import com.company.efood.sys.entity.Category;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "CATALOG_PRODUCT")
public class CatalogProduct extends BaseEntity {

    @Column(name = "PRODUCT_NAME", nullable = false)
    private String productName;

    @Column(name = "PRODUCT_TYPE")
    private String productType = "GENERAL";

    @Column(name = "SERVICE_TYPE")
    private String serviceType;

    @Column(name = "PRODUCT_NAME_BN")
    private String productNameBn;

    @Column(name = "SKU", unique = true)
    private String sku;

    @Column(name = "BARCODE", unique = true)
    private String barcode;

    @Column(name = "DESCRIPTION", columnDefinition = "text")
    private String description;

    @Column(name = "DEFAULT_PRICE")
    private BigDecimal defaultPrice;

    @Column(name = "DEFAULT_DISCOUNT_PRICE")
    private BigDecimal defaultDiscountPrice;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "BRAND")
    private String brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private Category category;

    @Column(name = "IS_ACTIVE_FOR_ALL_SHOPS")
    private Boolean isActiveForAllShops = true;

    @Column(name = "IS_DIGITAL")
    private Boolean isDigital = false;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @ElementCollection
    @CollectionTable(name = "CATALOG_PRODUCT_TAGS", joinColumns = @JoinColumn(name = "PRODUCT_ID"))
    @Column(name = "TAG")
    private List<String> tags;
}
