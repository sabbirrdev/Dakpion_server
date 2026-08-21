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
@Table(name = "DELIVERY_FEE_POLICY")
public class DeliveryFeePolicy extends BaseEntity {

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "BASE_FEE", nullable = false)
    private Double baseFee;

    @Column(name = "PER_KM_RATE")
    private Double perKmRate;

    @Column(name = "MIN_DISTANCE_KM")
    private Double minDistanceKm;

    @Column(name = "MAX_DISTANCE_KM")
    private Double maxDistanceKm;

    @Column(name = "EFFECTIVE_FROM")
    private LocalDate effectiveFrom;

    @Column(name = "EFFECTIVE_TO")
    private LocalDate effectiveTo;

    @Column(name = "DESCRIPTION")
    private String description;
}
