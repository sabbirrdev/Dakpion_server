package com.company.efood.user.entity;

import com.company.efood.sys.entity.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "CART_ITEM")
public class CartItem extends BasePurchasableItem {

    @OneToMany(mappedBy = "cartItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemAddon> addons;

    @Column(name = "CART_KEY") // for guest carts
    private String cartKey;

    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID")
    private Customer customer;

    @Column(name = "BRANCH_ID")
    private Long branchId; // optional for


}
