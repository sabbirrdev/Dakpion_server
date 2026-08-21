package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "RIDE_REQUEST")
public class RideRequest extends BaseEntity {

    @Column(name = "CUSTOMER_ID", nullable = false)
    private Long customerId;

    @Column(name = "PICKUP_ADDRESS", nullable = false)
    private String pickupAddress;

    @Column(name = "PICKUP_LAT")
    private Double pickupLat;

    @Column(name = "PICKUP_LON")
    private Double pickupLon;

    @Column(name = "DROP_ADDRESS", nullable = false)
    private String dropAddress;

    @Column(name = "DROP_LAT")
    private Double dropLat;

    @Column(name = "DROP_LON")
    private Double dropLon;

    @Column(name = "DISTANCE_KM", nullable = false)
    private Double distanceKm;

    @Column(name = "VEHICLE_TYPE", nullable = false)
    private String vehicleType;

    @Column(name = "BASE_FARE", nullable = false)
    private Double baseFare;

    @Column(name = "DISTANCE_FARE", nullable = false)
    private Double distanceFare;

    @Column(name = "TOTAL_FARE", nullable = false)
    private Double totalFare;

    @Column(name = "COMMISSION_AMOUNT", nullable = false)
    private Double commissionAmount;

    @Column(name = "RIDER_EARNINGS", nullable = false)
    private Double riderEarnings;

    @Column(name = "RIDER_BONUS", nullable = false)
    private Double riderBonus;

    @Column(name = "STATUS", nullable = false)
    private String status = "REQUESTED"; // REQUESTED, ACCEPTED, ON_THE_WAY, COMPLETED, CANCELLED

    @Column(name = "RAIDER_ID")
    private Long raiderId;

    @Column(name = "PAYMENT_METHOD")
    private String paymentMethod = "COD";
}
