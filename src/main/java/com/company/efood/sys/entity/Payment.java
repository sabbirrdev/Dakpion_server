package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import com.company.efood.sys.utils.PaymentMethod;
import com.company.efood.sys.utils.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "PAYMENT")
@EqualsAndHashCode(callSuper = true)
public class Payment extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_METHOD", nullable = false)
    private PaymentMethod paymentMethod; // e.g., COD, CARD, BKASH

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_STATUS", nullable = false)
    private PaymentStatus status; // PAID, PENDING, FAILED

    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @Column(name = "TRANSACTION_ID",  unique = true, nullable = false)
    private String transactionId;

    @OneToOne
    @JoinColumn(name = "ORDER_ID", unique = true, nullable = false)
    private Order order;
}

