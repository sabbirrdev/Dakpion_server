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
@Table(name = "dakpion_faq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DakpionFaqEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "question_en", nullable = false, columnDefinition = "TEXT")
    private String questionEn;

    @Column(name = "question_bn", nullable = false, columnDefinition = "TEXT")
    private String questionBn;

    @Column(name = "answer_en", nullable = false, columnDefinition = "TEXT")
    private String answerEn;

    @Column(name = "answer_bn", nullable = false, columnDefinition = "TEXT")
    private String answerBn;

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
