package com.company.efood.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTokenDto {
    private Long id;
    
    @NotBlank(message = "FCM token is required")
    private String fcmToken;
    
    private String deviceType; // ANDROID, IOS, WEB
}
