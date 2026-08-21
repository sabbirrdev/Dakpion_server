package com.company.efood.notification.controller;

import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.config.CurrentUserContext;
import com.company.efood.notification.entity.NotificationLog;
import com.company.efood.notification.repository.NotificationLogRepo;
import com.company.efood.sys.utils.AppUserType;
import com.company.efood.sys.utils.AuthTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.company.efood.base.BaseConstants.*;

@Slf4j
@RestController
@RequestMapping({PRIVET_ENDPOINT + "notifications", SYSTEM_ADMIN_END_POINT + "notifications"})
@RequiredArgsConstructor
public class NotificationLogController {

    private final NotificationLogRepo notificationLogRepo;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;

    @GetMapping("/logs")
    public ResponseEntity<BaseResponse> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request
    ) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            AppUserType userType = CurrentUserContext.getRole();

            Page<NotificationLog> logs;
            if (AppUserType.SYSTEM_ADMIN.equals(userType) || AppUserType.DEVELOPER.equals(userType)) {
                logs = notificationLogRepo.findAllByOrderByEntryDateDesc(PageRequest.of(page, size));
            } else {
                logs = notificationLogRepo.findByAppUserIdOrderByEntryDateDesc(userId, PageRequest.of(page, size));
            }

            return ResponseEntity.ok(baseUtils.generateSuccessResponse(logs, PROCESS_COMPLETE, PROCESS_COMPLETE_BN));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(baseUtils.generateErrorResponse(e));
        }
    }

    @GetMapping("/unread-count")
    public ResponseEntity<BaseResponse> getUnreadCount(HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            AppUserType userType = CurrentUserContext.getRole();

            long count;
            if (AppUserType.SYSTEM_ADMIN.equals(userType) || AppUserType.DEVELOPER.equals(userType)) {
                count = notificationLogRepo.countByAppUserTypeAndIsReadFalse(AppUserType.SYSTEM_ADMIN);
            } else {
                count = notificationLogRepo.countByAppUserIdAndIsReadFalse(userId);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("unreadCount", count);
            return ResponseEntity.ok(baseUtils.generateSuccessResponse(data, PROCESS_COMPLETE, PROCESS_COMPLETE_BN));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(baseUtils.generateErrorResponse(e));
        }
    }

    @PostMapping("/mark-read/{id}")
    public ResponseEntity<BaseResponse> markRead(@PathVariable Long id) {
        try {
            notificationLogRepo.findById(id).ifPresent(log -> {
                log.setIsRead(true);
                log.setReadAt(LocalDateTime.now());
                notificationLogRepo.save(log);
            });
            return ResponseEntity.ok(baseUtils.generateSuccessResponse("Notification marked as read", PROCESS_COMPLETE, PROCESS_COMPLETE_BN));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(baseUtils.generateErrorResponse(e));
        }
    }
}
