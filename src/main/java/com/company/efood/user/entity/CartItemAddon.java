package com.company.efood.user.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "CART_ITEM_ADDON")
public class CartItemAddon extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CART_ITEM_ID", nullable = false)
    private CartItem cartItem; // The cart item this addon belongs to

    // Store original ProductAddon ID and its details for consistency
    @Column(nullable = false)
    private Long productAddonId; // Reference to the original ProductAddon ID

    @Column(nullable = false, length = 100)
    private String name; // Name of the addon at the time it was added to cart

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Price of the addon at the time it was added to cart

}
