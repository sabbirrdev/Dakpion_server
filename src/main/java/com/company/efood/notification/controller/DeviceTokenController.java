package com.company.efood.notification.controller;

import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.config.CurrentUserContext;
import com.company.efood.notification.dto.DeviceTokenDto;
import com.company.efood.notification.entity.DeviceToken;
import com.company.efood.notification.service.FcmNotificationService;
import com.company.efood.sys.utils.AppUserType;
import com.company.efood.sys.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.company.efood.base.BaseConstants.PRIVET_ENDPOINT;
import static com.company.efood.base.BaseConstants.PROCESS_COMPLETE;
import static com.company.efood.base.BaseConstants.PROCESS_COMPLETE_BN;

@Slf4j
@RestController
@RequestMapping({PRIVET_ENDPOINT + "notification/tokens", PRIVET_ENDPOINT + "device"})
@RequiredArgsConstructor
public class DeviceTokenController {

    private final FcmNotificationService fcmNotificationService;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;

    /**
     * POST /api/private/notification/tokens/register
     * Securely registers or updates an FCM device token for the authenticated user.
     */
    @PostMapping({"/register", "/register-token"})
    public ResponseEntity<BaseResponse> registerToken(
            @Valid @RequestBody DeviceTokenDto dto,
            HttpServletRequest request
    ) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            AppUserType userType = CurrentUserContext.getRole();
            if (userType == null) {
                userType = AppUserType.CUSTOMER;
            }

            log.info("📱 [FCM] Registering device token for userId={}, userType={}, deviceType={}",
                    userId, userType, dto.getDeviceType());

            DeviceToken token = fcmNotificationService.registerToken(userId, userType, dto);
            BaseResponse response = baseUtils.generateSuccessResponse(token, PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ [FCM] Failed to register device token: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(baseUtils.generateErrorResponse(e));
        }
    }

    /**
     * POST /api/private/notification/tokens/unregister
     * Unregisters an FCM device token when user logs out or switches devices.
     */
    @PostMapping({"/unregister", "/unregister-token"})
    public ResponseEntity<BaseResponse> unregisterToken(
            @RequestParam(name = "fcmToken", required = false) String tokenParam,
            @RequestBody(required = false) Map<String, String> body
    ) {
        try {
            String token = tokenParam;
            if ((token == null || token.isBlank()) && body != null) {
                token = body.get("fcmToken");
            }

            if (token != null && !token.isBlank()) {
                log.info("📱 [FCM] Unregistering device token: {}",
                        token.length() > 10 ? token.substring(0, 10) + "..." : token);
                fcmNotificationService.unregisterToken(token);
            }

            BaseResponse response = baseUtils.generateSuccessResponse(
                    "Token unregistered successfully",
                    PROCESS_COMPLETE,
                    PROCESS_COMPLETE_BN
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ [FCM] Failed to unregister device token: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(baseUtils.generateErrorResponse(e));
        }
    }
}
