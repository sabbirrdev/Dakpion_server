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
@Table(name = "dakpion_font")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DakpionFontEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "font_family", nullable = false)
    private String fontFamily;

    @Column(name = "category", length = 20, nullable = false)
    @Builder.Default
    private String category = "bengali";

    @Column(name = "css_url", length = 500)
    private String cssUrl;

    @Column(name = "preview_sample", nullable = false)
    private String previewSample;

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
