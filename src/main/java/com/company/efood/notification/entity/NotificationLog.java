package com.company.efood.notification.entity;

import com.company.efood.base.BaseEntity;
import com.company.efood.sys.utils.AppUserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICATION_LOG", indexes = {
        @Index(name = "idx_notif_user", columnList = "app_user_id"),
        @Index(name = "idx_notif_type", columnList = "app_user_type"),
        @Index(name = "idx_notif_order", columnList = "order_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLog extends BaseEntity {

    @Column(name = "app_user_id")
    private Long appUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "app_user_type", length = 30)
    private AppUserType appUserType;

    @Column(name = "title", length = 250, nullable = false)
    private String title;

    @Column(name = "body", length = 1000)
    private String body;

    @Column(name = "notification_type", length = 50)
    private String notificationType; // "ORDER_PLACED", "ORDER_PREPARING", "ORDER_ASSIGNED", "ORDER_STATUS_UPDATE", "NEW_REGISTRATION", etc.

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "status", length = 20)
    private String status; // "SENT", "FAILED", "PENDING"

    @Column(name = "fcm_message_id", length = 250)
    private String fcmMessageId;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Builder.Default
    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}
