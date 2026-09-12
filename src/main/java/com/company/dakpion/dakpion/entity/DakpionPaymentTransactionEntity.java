package com.company.dakpion.dakpion.entity;

import com.company.dakpion.dakpion.constant.PaymentMethod;
import com.company.dakpion.dakpion.constant.PaymentStatus;
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
import java.util.UUID;

@Entity
@Table(name = "dakpion_payment_transaction")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DakpionPaymentTransactionEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", length = 100, nullable = false, unique = true)
    private String transactionId;

    @OneToOne
    @JoinColumn(name = "letter_id", unique = true, nullable = false)
    private DakpionLetterEntity letter;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", length = 50, nullable = false)
    private PaymentMethod provider;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 10, nullable = false)
    @Builder.Default
    private String currency = "BDT";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.UNPAID;

    @Column(name = "gateway_reference", length = 255)
    private String gatewayReference;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_payload", columnDefinition = "jsonb")
    private String rawPayload;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
