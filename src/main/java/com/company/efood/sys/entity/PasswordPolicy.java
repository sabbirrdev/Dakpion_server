package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "SYA_PASSWORD_POLICY")
public class PasswordPolicy extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    @Column(name = "MIN_LENGTH", nullable = false)
    private Integer minLength;

    @Column(name = "SEQUENTIAL", columnDefinition = "boolean default false")
    private Boolean sequential = false;

    @Column(name = "SPECIAL_CHAR", columnDefinition = "boolean default false")
    private Boolean specialChar = false;

    @Column(name = "ALPHANUMERIC", columnDefinition = "boolean default false")
    private Boolean alphanumeric = false;

    @Column(name = "UPPER_LOWER", columnDefinition = "boolean default false")
    private Boolean upperLower = false;

    @Column(name = "MATCH_USERNAME", columnDefinition = "boolean default false")
    private Boolean matchUsername = false;

    @Column(name = "PASSWORD_REMEMBER")
    private Integer passwordRemember;

    @Column(name = "PASSWORD_AGE")
    private Integer passwordAge;

    @Column(name = "DEV_CODE")
    private Integer devCode;
}
