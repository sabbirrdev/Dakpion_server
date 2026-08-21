package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import com.company.efood.sys.utils.DiscountType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "COUPONS")
@EqualsAndHashCode(callSuper = true)
public class Coupon extends BaseEntity {

    @Column(name = "CODE", unique = true)
    private String code;

    @Column(name = "DESCRIPTION")
    private String description;

    @Enumerated(EnumType.STRING)
    private DiscountType type; // FIXED, PERCENTAGE

    @Column(name = "VALUE", nullable = false)
    private BigDecimal value;

    @Column(name = "EXPIRY_DATE")
    private LocalDateTime expiryDate;

    @Column(name = "MIN_ORDER_AMOUNT")
    private BigDecimal minOrderAmount;

    @Column(name = "MAX_USAGE")
    private Integer maxUsage;

    @Column(name = "USED_COUNT")
    private Integer usedCount = 0;
}

