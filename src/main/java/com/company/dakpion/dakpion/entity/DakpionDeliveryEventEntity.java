package com.company.dakpion.dakpion.entity;

import com.company.dakpion.dakpion.constant.DeliveryEventStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dakpion_delivery_event")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DakpionDeliveryEventEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "letter_id", nullable = false)
    private UUID letterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private DeliveryEventStatus status;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    /**
     * Who created this event: e.g. "SYSTEM", "ADMIN:42", "COURIER_WEBHOOK"
     */
    @Column(name = "created_by", length = 100, nullable = false)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
}
