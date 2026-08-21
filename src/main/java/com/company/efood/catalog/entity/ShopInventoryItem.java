package com.company.efood.catalog.entity;

import com.company.efood.base.BaseEntity;
import com.company.efood.sys.entity.Branch;
import com.company.efood.sys.entity.Shop;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "SHOP_INVENTORY_ITEM")
public class ShopInventoryItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATALOG_PRODUCT_ID", nullable = false)
    private CatalogProduct catalogProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHOP_ID", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BRANCH_ID")
    private Branch branch;

    @Column(name = "SELLING_PRICE")
    private BigDecimal sellingPrice;

    @Column(name = "DISCOUNT_PRICE")
    private BigDecimal discountPrice;

    @Column(name = "AVAILABLE_QTY")
    private Integer availableQty = 0;

    @Column(name = "MIN_QTY")
    private Integer minQty = 0;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @Column(name = "IS_DEFAULT_FOR_SHOP")
    private Boolean isDefaultForShop = false;

    @Column(name = "CUSTOM_NAME")
    private String customName;

    @Column(name = "CUSTOM_DESCRIPTION", columnDefinition = "text")
    private String customDescription;
}
