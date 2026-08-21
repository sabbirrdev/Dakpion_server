package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Table(name = "PRODUCT_VARIANT")
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductVariant extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    @Column(name = "VARIANT_NAME", nullable = false)
    private String variantName; // e.g., "Large", "Double Patty"

    @Column(name = "EXTRA_PRICE", nullable = false)
    private BigDecimal extraPrice;
}

