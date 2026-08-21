package com.company.efood.user.controller;

import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.user.entity.CustomerVisitorLog;
import com.company.efood.user.repository.CustomerVisitorLogRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static com.company.efood.base.BaseConstants.PUBLIC_ENDPOINT;

@RestController
@RequestMapping(PUBLIC_ENDPOINT + "visitor")
@AllArgsConstructor
public class CustomerVisitorController {

    private final CustomerVisitorLogRepo visitorLogRepo;
    private final BaseUtils baseUtils;

    @PostMapping("/log")
    public BaseResponse logVisitor(@RequestBody(required = false) Map<String, String> body, HttpServletRequest request) {
        try {
            String visitorUuid = (body != null && body.get("visitorUuid") != null)
                    ? body.get("visitorUuid")
                    : UUID.randomUUID().toString();

            String appType = (body != null && body.get("appType") != null)
                    ? body.get("appType")
                    : "CUSTOMER_APP";

            String ipAddress = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");

            CustomerVisitorLog log = visitorLogRepo.findByVisitorUuid(visitorUuid).orElseGet(() -> {
                CustomerVisitorLog n = new CustomerVisitorLog();
                n.setVisitorUuid(visitorUuid);
                n.setEntryUser(0L);
                n.setEntryDate(LocalDateTime.now());
                return n;
            });

            log.setIpAddress(ipAddress);
            log.setAppType(appType);
            if (userAgent != null && userAgent.length() > 250) {
                userAgent = userAgent.substring(0, 250);
            }
            log.setUserAgent(userAgent);
            log.setActive(true);

            visitorLogRepo.save(log);

            long totalVisitors = visitorLogRepo.countDistinctVisitors();

            return baseUtils.generateSuccessResponse(Map.of(
                    "visitorUuid", visitorUuid,
                    "totalVisitors", totalVisitors
            ), "Visitor logged successfully", "ভিজিটর সঠিকভাবে নথিভুক্ত হয়েছে");

        } catch (Exception e) {
            return baseUtils.generateErrorResponse(e);
        }
    }
}
