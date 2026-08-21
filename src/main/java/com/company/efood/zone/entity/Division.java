package com.company.efood.zone.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "SYA_DIVISION")
public class Division extends BaseEntity {

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "NAME_BN")
    private String nameBn;
}
