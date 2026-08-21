package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "COMMISSION_POLICY")
public class CommissionPolicy extends BaseEntity {

    @Column(name = "NAME", nullable = false)
    private String name;

    // Percentage-based commission (e.g., 10%)
    @Column(name = "PERCENTAGE")
    private Double percentage;

    // Fixed commission amount
    @Column(name = "FIXED_AMOUNT")
    private Double fixedAmount;

    // Indicates if this policy has dynamic conditions
    @Column(name = "IS_DYNAMIC", nullable = false)
    private Boolean isDynamic = false;

    // Optional: Only applies if order total is within these limits
    @Column(name = "MIN_ORDER_AMOUNT")
    private Double minOrderAmount;

    @Column(name = "MAX_ORDER_AMOUNT")
    private Double maxOrderAmount;

    // Time-based activation
    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    // Soft delete flag
    @Column(name = "IS_DELETED", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "DESCRIPTION")
    private String description;

    // Optional: expression-based condition for advanced rules
    @Column(name = "CONDITION_EXPRESSION")
    private String conditionExpression;
}

