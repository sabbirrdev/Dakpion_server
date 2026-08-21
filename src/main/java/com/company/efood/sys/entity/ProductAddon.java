package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Table(name = "PRODUCT_ADDON")
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductAddon extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @Column(name = "ADDON_NAME", nullable = false)
    private String addonName; // e.g., "Extra Cheese"

    @Column(name = "PRICE", nullable = false)
    private BigDecimal price;
}

