package com.company.efood.user.entity;

import com.company.efood.base.BaseEntity;
import com.company.efood.sys.entity.Order;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "LOYALTY_TRANSACTION", indexes = {
        @Index(name = "idx_loyalty_tx_customer", columnList = "CUSTOMER_ID"),
        @Index(name = "idx_loyalty_tx_order", columnList = "ORDER_ID"),
        @Index(name = "idx_loyalty_tx_order_type", columnList = "ORDER_ID, TYPE")
})
@NoArgsConstructor
public class LoyaltyTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false, length = 20)
    private LoyaltyTransactionType type;

    @Column(name = "POINTS", nullable = false)
    private Integer points;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    public LoyaltyTransaction(Customer customer, Order order, LoyaltyTransactionType type, Integer points, String description) {
        this.customer = customer;
        this.order = order;
        this.type = type;
        this.points = points;
        this.description = description;
    }
}
