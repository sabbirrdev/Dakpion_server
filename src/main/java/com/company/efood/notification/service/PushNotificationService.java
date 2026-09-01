package com.company.efood.notification.service;

import com.company.efood.notification.entity.UserDeviceToken;
import com.company.efood.notification.repository.DeviceTokenRepo;
import com.company.efood.notification.repository.UserDeviceTokenRepo;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final UserDeviceTokenRepo userDeviceTokenRepo;
    private final DeviceTokenRepo deviceTokenRepo;

    /**
     * Dispatches notification and data payload directly to a specific FCM token.
     */
    public boolean sendDirectPushNotification(
            String fcmToken,
            String title,
            String body,
            Map<String, String> dataPayload
    ) {
        if (FirebaseApp.getApps().isEmpty()) {
            log.warn("⚠️ [FCM] FirebaseApp is not initialized. Cannot dispatch push notification.");
            return false;
        }

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message.Builder builder = Message.builder()
                .setToken(fcmToken)
                .setNotification(notification);

        if (dataPayload != null && !dataPayload.isEmpty()) {
            builder.putAllData(dataPayload);
        }

        builder.setAndroidConfig(AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .setChannelId("ekhanei_high_importance_channel")
                        .setSound("default")
                        .build())
                .build());

        builder.setApnsConfig(ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setSound("default")
                        .setContentAvailable(true)
                        .build())
                .build());

        try {
            String messageId = FirebaseMessaging.getInstance().send(builder.build());
            log.info("🚀 [FCM] Push dispatched successfully. Message ID: {}", messageId);
            return true;
        } catch (FirebaseMessagingException e) {
            handleFcmException(fcmToken, e);
            return false;
        } catch (Exception e) {
            log.error("❌ [FCM] Unexpected error sending message to token [{}]: {}", fcmToken, e.getMessage());
            return false;
        }
    }

    /**
     * Sends push notification to all active devices registered for a user & app type.
     */
    public void sendToUser(Long userId, UserDeviceToken.AppType appType, String title, String body, Map<String, String> dataPayload) {
        List<UserDeviceToken> tokens = userDeviceTokenRepo.findByUserIdAndAppTypeAndIsActiveTrue(userId, appType);
        if (tokens.isEmpty()) {
            log.info("ℹ️ [FCM] No active device tokens found for userId={} appType={}", userId, appType);
            return;
        }

        for (UserDeviceToken token : tokens) {
            sendDirectPushNotification(token.getFcmToken(), title, body, dataPayload);
        }
    }

    private void handleFcmException(String token, FirebaseMessagingException e) {
        MessagingErrorCode errorCode = e.getMessagingErrorCode();
        log.error("❌ [FCM] Failure sending to token [{}]: ErrorCode={}, Message={}", token, errorCode, e.getMessage());

        if (errorCode == MessagingErrorCode.UNREGISTERED || errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
            log.warn("🧹 [FCM] Token [{}] is unregistered or invalid. Cleaning up from database...", token);
            try {
                userDeviceTokenRepo.deleteByFcmToken(token);
                deviceTokenRepo.deleteByFcmToken(token);
            } catch (Exception dbEx) {
                log.warn("Could not delete stale token from DB: {}", dbEx.getMessage());
            }
        }
    }
}
