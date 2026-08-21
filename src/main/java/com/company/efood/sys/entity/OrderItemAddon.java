package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Table(name = "ORDER_ITEM_ADDON")
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderItemAddon extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "ORDER_ITEM_ID", nullable = false)
    private OrderItem orderItem;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ADDON_ID", nullable = false)
    private ProductAddon productAddon;

    @Column(name = "PRICE", nullable = false)
    private BigDecimal price;
}

