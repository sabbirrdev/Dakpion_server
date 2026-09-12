package com.company.dakpion.dakpion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "dakpion_testimonial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DakpionTestimonialEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "author_nickname", nullable = false)
    private String authorNickname;

    @Column(name = "quote_en", nullable = false, columnDefinition = "TEXT")
    private String quoteEn;

    @Column(name = "quote_bn", nullable = false, columnDefinition = "TEXT")
    private String quoteBn;

    @Column(name = "city_en", nullable = false)
    private String cityEn;

    @Column(name = "city_bn", nullable = false)
    private String cityBn;

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
