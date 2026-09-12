package com.company.dakpion.dakpion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "dakpion_pricing_plan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DakpionPricingPlanEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "name_bn", nullable = false)
    private String nameBn;

    @Column(name = "tagline_en", nullable = false)
    private String taglineEn;

    @Column(name = "tagline_bn", nullable = false)
    private String taglineBn;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "billing_unit_en", nullable = false)
    private String billingUnitEn;

    @Column(name = "billing_unit_bn", nullable = false)
    private String billingUnitBn;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "features", columnDefinition = "jsonb", nullable = false)
    private String features;

    @Column(name = "highlighted")
    @Builder.Default
    private Boolean highlighted = false;

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
