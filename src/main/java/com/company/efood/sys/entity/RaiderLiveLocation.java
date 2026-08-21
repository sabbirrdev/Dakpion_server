package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import com.company.efood.raider.entity.Raider;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "RAIDER_LOCATION")
@EqualsAndHashCode(callSuper = true)
public class RaiderLiveLocation extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "RAIDER_ID", unique = true)
    private Raider raider;

    @Column(name = "LAT", nullable = false)
    private Double lat;

    @Column(name = "LNG", nullable = false)
    private Double lng;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
}
