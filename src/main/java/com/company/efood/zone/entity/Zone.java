package com.company.efood.zone.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "SYA_ZONE")
public class Zone extends BaseEntity {

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "NAME_BN")
    private String nameBn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UPAZILA_ID", nullable = false)
    private Upazila upazila;

    @Column(name = "BASE_DELIVERY_FEE", nullable = false)
    private BigDecimal baseDeliveryFee = BigDecimal.ZERO;

    @Column(name = "PER_KM_CHARGE", nullable = false)
    private BigDecimal perKmCharge = BigDecimal.ZERO;

    @Column(name = "HUB_LAT")
    private Double hubLat;

    @Column(name = "HUB_LON")
    private Double hubLon;

    @Column(name = "GEOFENCE_POLYGON", columnDefinition = "text")
    private String geoFencePolygon;

    @Column(name = "IS_DELIVERABLE", columnDefinition = "boolean default true")
    private Boolean isDeliverable = true;
}
