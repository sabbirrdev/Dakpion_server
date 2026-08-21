package com.company.efood.sys.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "EXCLUSIVE_OFFER")
@EqualsAndHashCode(callSuper = true)
public class ExclusiveOffer extends BaseEntity {

    @Column(name = "CODE", nullable = false)
    private String code;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "DESCRIPTION", columnDefinition = "text")
    private String description;

    @Column(name = "TAG")
    private String tag;

    @Column(name = "DISCOUNT_VALUE")
    private BigDecimal discountValue;

    @Column(name = "DISCOUNT_TYPE")
    private String discountType;

    @Column(name = "MIN_ORDER_AMOUNT")
    private BigDecimal minOrderAmount;

    @Column(name = "BANNER_URL")
    private String bannerUrl;

    @Column(name = "COLOR_HEX")
    private String colorHex;

    @Column(name = "VALID_TILL")
    private String validTill;

    @Column(name = "EXPIRY_DATE")
    private LocalDateTime expiryDate;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;
}
