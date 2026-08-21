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
@Table(name = "RIDE_FARE_POLICY")
public class RideFarePolicy extends BaseEntity {

    @Column(name = "VEHICLE_TYPE", nullable = false)
    private String vehicleType; // BIKE, BICYCLE, CAR, CNG, COVERED_VAN

    @Column(name = "BASE_FARE", nullable = false)
    private Double baseFare = 30.0;

    @Column(name = "PER_KM_RATE", nullable = false)
    private Double perKmRate = 15.0;

    @Column(name = "MINIMUM_FARE", nullable = false)
    private Double minimumFare = 40.0;

    @Column(name = "PLATFORM_COMMISSION_PERCENT", nullable = false)
    private Double platformCommissionPercent = 10.0;

    @Column(name = "RIDER_BONUS_PER_RIDE", nullable = false)
    private Double riderBonusPerRide = 5.0;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean active = true;

    @Column(name = "DESCRIPTION")
    private String description;
}
