package com.company.efood.sys.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "ORDER_ITEM")
public class OrderItem extends BasePurchasableItem {
    @ManyToOne
    @JoinColumn(name = "ORDER_ID", nullable = false)
    private Order order;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemAddon> addons;

    @Column(name = "CUSTOM_NOTE")
    private String customNote;

}

