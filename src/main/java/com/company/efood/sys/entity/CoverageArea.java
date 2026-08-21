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
@Table(name = "COVERAGE_AREA")
public class CoverageArea extends BaseEntity {

    @Column(name = "AREA_NAME", nullable = false)
    private String areaName;

    @Column(name = "DISTRICT")
    private String district;

    @Column(name = "POLICE_STATION")
    private String policeStation;

    @Column(name = "DELIVERY_CHARGE", nullable = false)
    private Double deliveryCharge = 10.0;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean active = true;

    @Column(name = "DESCRIPTION")
    private String description;
}
