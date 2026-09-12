package com.company.dakpion.dakpion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "dakpion_blocked_word")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DakpionBlockedWordEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "term", length = 255, nullable = false, unique = true)
    private String term;

    @Column(name = "language", length = 10, nullable = false)
    @Builder.Default
    private String language = "ALL";

    @Column(name = "category", length = 50)
    @Builder.Default
    private String category = "PROFANITY";

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
