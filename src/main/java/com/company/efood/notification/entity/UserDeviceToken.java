package com.company.efood.notification.entity;

import com.company.efood.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER_DEVICE_TOKEN", indexes = {
        @Index(name = "idx_udt_user_app", columnList = "user_id, app_type"),
        @Index(name = "idx_udt_fcm_token", columnList = "fcm_token")
})
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceToken extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "fcm_token", length = 512, nullable = false)
    private String fcmToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "app_type", length = 30, nullable = false)
    private AppType appType; // CUSTOMER, RIDER, SELLER

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "device_os", length = 50)
    private String deviceOs;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    public enum AppType {
        CUSTOMER,
        RIDER,
        SELLER
    }
}
