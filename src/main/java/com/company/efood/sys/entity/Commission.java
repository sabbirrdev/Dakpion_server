package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "COMMISSION")
public class Commission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHOP_ID", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false)
    private Order order;

    @Column(name = "ORDER_ID", insertable = false, updatable = false) // if relations exists
    private Long orderId;

    @Column(name = "ORDER_TOTAL", nullable = false)
    private java.math.BigDecimal orderTotal;

    @Column(name = "COMMISSION_RATE", nullable = false)
    private java.math.BigDecimal commissionRate;

    @Column(name = "COMMISSION_AMOUNT", nullable = false)
    private java.math.BigDecimal commissionAmount;

    @Column(name = "DELIVERY_FEE")
    private java.math.BigDecimal deliveryFee;

    @Column(name = "MERCHANT_PAYOUT", nullable = false)
    private java.math.BigDecimal merchantPayout;

    @Column(name = "SHOP_TYPE")
    private String shopType;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "PAID", nullable = false)
    private Boolean paid = false;

    @Column(name = "NOTES")
    private String notes;
}
