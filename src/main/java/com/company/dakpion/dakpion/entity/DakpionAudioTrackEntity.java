package com.company.dakpion.dakpion.entity;

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
@Table(name = "dakpion_audio_track")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DakpionAudioTrackEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "name_bn", nullable = false)
    private String nameBn;

    @Column(name = "category_en", nullable = false)
    private String categoryEn;

    @Column(name = "category_bn", nullable = false)
    private String categoryBn;

    @Column(name = "src", length = 500, nullable = false)
    private String src;

    @Column(name = "duration_seconds", nullable = false)
    @Builder.Default
    private Integer durationSeconds = 0;

    @Column(name = "is_premium", nullable = false)
    @Builder.Default
    private Boolean isPremium = false;

    // AudioTrackEntity — was implicitly "premium = flat 15" in the old frontend mock; make it real
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

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
