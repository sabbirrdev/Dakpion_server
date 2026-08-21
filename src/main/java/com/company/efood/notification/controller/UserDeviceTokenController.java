package com.company.efood.notification.controller;

import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.notification.entity.UserDeviceToken;
import com.company.efood.notification.repository.UserDeviceTokenRepo;
import com.company.efood.sys.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.company.efood.base.BaseConstants.PRIVET_ENDPOINT;
import static com.company.efood.base.BaseConstants.PROCESS_COMPLETE;
import static com.company.efood.base.BaseConstants.PROCESS_COMPLETE_BN;

@Slf4j
@RestController
@RequestMapping({"/api/tokens", PRIVET_ENDPOINT + "notification/tokens", "/notification/tokens"})
@RequiredArgsConstructor
public class UserDeviceTokenController {

    private final UserDeviceTokenRepo userDeviceTokenRepo;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> registerToken(
            @RequestBody TokenRegisterRequest requestDto,
            HttpServletRequest request
    ) {
        try {
            Long userId = requestDto.getUserId();
            if (userId == null) {
                try {
                    userId = authTokenUtils.getUserIdFromRequest(request);
                } catch (Exception ignored) {}
            }
            if (userId == null) {
                userId = 0L;
            }

            if (requestDto.getFcmToken() == null || requestDto.getFcmToken().isBlank()) {
                return ResponseEntity.badRequest().body(
                        BaseResponse.builder()
                                .status(false)
                                .statusCode(400)
                                .message("fcmToken is required")
                                .build()
                );
            }

            UserDeviceToken.AppType appType = UserDeviceToken.AppType.CUSTOMER;
            if (requestDto.getAppType() != null) {
                try {
                    appType = UserDeviceToken.AppType.valueOf(requestDto.getAppType().trim().toUpperCase());
                } catch (Exception ignored) {}
            }

            Optional<UserDeviceToken> existing = userDeviceTokenRepo.findByFcmToken(requestDto.getFcmToken());
            UserDeviceToken token = existing.orElseGet(UserDeviceToken::new);
            token.setUserId(userId);
            token.setFcmToken(requestDto.getFcmToken());
            token.setAppType(appType);
            token.setIsActive(true);
            token.setDeviceOs(requestDto.getDeviceOs() != null ? requestDto.getDeviceOs() : "ANDROID");
            token.setLastUpdatedAt(LocalDateTime.now());
            if (token.getEntryDate() == null) {
                token.setEntryDate(LocalDateTime.now());
                token.setEntryUser(userId);
            }

            UserDeviceToken saved = userDeviceTokenRepo.save(token);
            log.info("📲 [TOKEN REGISTER] Registered FCM token for userId={}, appType={}", userId, appType);

            return ResponseEntity.ok(baseUtils.generateSuccessResponse(saved, PROCESS_COMPLETE, PROCESS_COMPLETE_BN));
        } catch (Exception e) {
            log.error("Token registration failure", e);
            return ResponseEntity.badRequest().body(baseUtils.generateErrorResponse(e));
        }
    }

    @PostMapping("/unregister")
    public ResponseEntity<BaseResponse> unregisterToken(@RequestBody TokenRegisterRequest requestDto) {
        try {
            if (requestDto.getFcmToken() != null) {
                userDeviceTokenRepo.deleteByFcmToken(requestDto.getFcmToken());
            }
            return ResponseEntity.ok(baseUtils.generateSuccessResponse("Token unregistered", PROCESS_COMPLETE, PROCESS_COMPLETE_BN));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(baseUtils.generateErrorResponse(e));
        }
    }

    @Data
    public static class TokenRegisterRequest {
        private Long userId;
        private String fcmToken;
        private String appType; // "CUSTOMER", "RIDER", "SELLER"
        private String deviceOs;
        private String deviceType;
    }
}
