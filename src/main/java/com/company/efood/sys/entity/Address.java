package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import com.company.efood.sys.utils.AddressType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ADDRESS_BOOK")
public class Address extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "ADDRESS_TYPE")
    private AddressType addressType = AddressType.DEFAULT;

    @Column(name = "DISTRICT",nullable = false)
    private String district;
    @Column(name = "POLICE_STATION",nullable = false)
    private String policeStation;
    @Column(name = "POST_OFFICE")
    private String postOffice;
    @Column(name = "LAT")
    private double lat;
    @Column(name = "LON")
    private double lon;
    @Column(name = "POST_CODE")
    private Number postCode;
    @Column(name = "HOUSE_NO")
    private String houseNo;
    @Column(name = "ROAD_NO")
    private Number roadNo;
    @Column(name = "ADDRESS",nullable = false)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ZONE_ID")
    private com.company.efood.zone.entity.Zone zone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UPAZILA_ID")
    private com.company.efood.zone.entity.Upazila upazila;
}
