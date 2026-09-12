package com.company.dakpion.dakpion.entity;

import com.company.dakpion.dakpion.constant.DeliveryType;
import com.company.dakpion.dakpion.constant.LetterStatus;
import com.company.dakpion.dakpion.constant.LocaleCode;
import com.company.dakpion.dakpion.constant.ModerationStatus;
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
@Table(name = "dakpion_letter")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DakpionLetterEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "sender_nickname", nullable = false)
    private String senderNickname;

    @Column(name = "sender_phone_hashed", nullable = false)
    private String senderPhoneHashed;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "recipient_phone")
    private String recipientPhone;

    @Column(name = "recipient_user_id")
    private Long recipientUserId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "theme_id", length = 50, nullable = false)
    private String themeId;

    @Column(name = "audio_id", length = 50, nullable = false)
    private String audioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type", length = 30, nullable = false)
    private DeliveryType deliveryType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "shipping_address", columnDefinition = "jsonb")
    private String shippingAddress;

    // DakpionLetterEntity — add these fields
    @Column(name = "theme_amount", precision = 10, scale = 2)
    private BigDecimal themeAmount;

    @Column(name = "audio_amount", precision = 10, scale = 2)
    private BigDecimal audioAmount;

    @Column(name = "delivery_amount", precision = 10, scale = 2)
    private BigDecimal deliveryAmount;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "currency", length = 3)
    @Builder.Default
    private String currency = "BDT";

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 30, nullable = false)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @OneToOne(mappedBy = "letter", cascade = CascadeType.ALL)
    private DakpionPaymentTransactionEntity payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", length = 30, nullable = false)
    @Builder.Default
    private ModerationStatus moderationStatus = ModerationStatus.PENDING;

    @Column(name = "moderation_reason", columnDefinition = "TEXT")
    private String moderationReason;

    @Column(name = "moderated_by")
    private Long moderatedBy;

    @Column(name = "moderated_at")
    private LocalDateTime moderatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private LetterStatus status = LetterStatus.SUBMITTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", length = 10, nullable = false)
    @Builder.Default
    private LocaleCode language = LocaleCode.en;

    @Column(name = "idempotency_key", length = 100)
    private String idempotencyKey;

    @Column(name = "short_code", length = 50, unique = true)
    private String shortCode;

    @Column(name = "tracking_code", length = 50, unique = true)
    private String trackingCode;

    @Column(name = "courier_booking_id", length = 100)
    private String courierBookingId;

    @Column(name = "courier_tracking_id", length = 100)
    private String courierTrackingId;

    @Column(name = "courier_status", length = 50)
    private String courierStatus;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "opened_at")
    private LocalDateTime openedAt;
}
