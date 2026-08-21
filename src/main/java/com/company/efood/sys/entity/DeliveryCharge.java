package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "DELIVERY_CHARGE")
public class DeliveryCharge extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DELIVERY_FEE_POLICY_ID", nullable = false)
    private DeliveryFeePolicy deliveryFeePolicy;

    @Column(name = "BASE_CHARGE", nullable = false)
    private Double baseCharge;

    @Column(name = "DISTANCE_KM")
    private Double distanceKm;

    @Column(name = "PER_KM_RATE")
    private Double perKmRate;

    @Column(name = "TOTAL_CHARGE", nullable = false)
    private Double totalCharge;

    @Column(name = "CALCULATED_AT", nullable = false)
    private LocalDateTime calculatedAt;
}
