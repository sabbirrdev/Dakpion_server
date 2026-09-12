package com.company.dakpion.dakpion.entity;

import com.company.dakpion.dakpion.constant.SmsPurpose;
import com.company.dakpion.dakpion.constant.SmsStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dakpion_sms_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DakpionSmsLogEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", length = 100)
    private String requestId;

    @Column(name = "to_number", length = 20, nullable = false)
    private String toNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", length = 50, nullable = false)
    private SmsPurpose purpose;

    @Column(name = "message_content", columnDefinition = "TEXT")
    private String messageContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    @Builder.Default
    private SmsStatus status = SmsStatus.PENDING;

    @Column(name = "error_code")
    private Integer errorCode;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "letter_id")
    private UUID letterId;

    @Column(name = "charge", precision = 8, scale = 4)
    private BigDecimal charge;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_checked_at")
    private LocalDateTime lastCheckedAt;
}
