package com.company.dakpion.dakpion.entity;

import com.company.dakpion.dakpion.constant.DeliveryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "dakpion_delivery_option")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DakpionDeliveryOptionEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30)
    private DeliveryType type;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "name_bn", nullable = false)
    private String nameBn;

    @Column(name = "description_en", nullable = false, columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "description_bn", nullable = false, columnDefinition = "TEXT")
    private String descriptionBn;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "eta_label_en", nullable = false)
    private String etaLabelEn;

    @Column(name = "eta_label_bn", nullable = false)
    private String etaLabelBn;

    @Column(name = "icon", length = 50, nullable = false)
    private String icon;

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
