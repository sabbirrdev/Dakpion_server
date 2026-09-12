package com.company.dakpion.dakpion.entity;

import com.company.dakpion.dakpion.constant.ThemeTier;
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
@Table(name = "dakpion_theme")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DakpionThemeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "name_bn", nullable = false)
    private String nameBn;

    @Column(name = "description_en", nullable = false, columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "description_bn", nullable = false, columnDefinition = "TEXT")
    private String descriptionBn;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", length = 20, nullable = false)
    private ThemeTier tier;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "palette", columnDefinition = "jsonb", nullable = false)
    private String palette;

    @Column(name = "preview_image", nullable = false)
    private String previewImage;

    @Column(name = "occasion_en")
    private String occasionEn;

    @Column(name = "occasion_bn")
    private String occasionBn;

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
