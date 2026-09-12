package com.company.dakpion.dakpion.controller;

import com.company.dakpion.base.BaseResponse;
import com.company.dakpion.base.BaseUtils;
import com.company.dakpion.dakpion.constant.DeliveryType;
import com.company.dakpion.dakpion.dto.PublicTrackingDto;
import com.company.dakpion.dakpion.entity.DakpionDeliveryEventEntity;
import com.company.dakpion.dakpion.entity.DakpionLetterEntity;
import com.company.dakpion.dakpion.exception.ResourceNotFoundException;
import com.company.dakpion.dakpion.repository.DakpionDeliveryEventRepo;
import com.company.dakpion.dakpion.repository.DakpionLetterRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.company.dakpion.base.BaseConstants.PROCESS_COMPLETE;
import static com.company.dakpion.base.BaseConstants.PROCESS_COMPLETE_BN;

/**
 * Public tracking endpoint for PHYSICAL DakPion letters.
 * No authentication required. Zero PII returned.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/track")
@RequiredArgsConstructor
public class DakpionTrackingController {

    private static final int MAX_REQUESTS_PER_MIN = 30;
    private static final long WINDOW_MILLIS = 60_000L;

    private final DakpionLetterRepo letterRepo;
    private final DakpionDeliveryEventRepo deliveryEventRepo;
    private final BaseUtils baseUtils;

    private static class RateLimitEntry {
        long windowStart;
        AtomicInteger count;

        RateLimitEntry(long windowStart) {
            this.windowStart = windowStart;
            this.count = new AtomicInteger(1);
        }
    }

    private final ConcurrentHashMap<String, RateLimitEntry> ipRateLimits = new ConcurrentHashMap<>();

    /**
     * GET /api/v1/track/{trackingCode}
     * Public. Rate-limited at 30 req/min per IP.
     * Returns 404 for unknown or non-physical letters (no PII leak).
     */
    @GetMapping("/{trackingCode}")
    public BaseResponse track(
            @PathVariable String trackingCode,
            HttpServletRequest httpRequest) {

        try {
            // IP-based in-memory rate limiting
            String clientIp = extractClientIp(httpRequest);
            if (!checkRateLimit(clientIp)) {
                return baseUtils.generateErrorResponse(new RuntimeException("Too many tracking requests. Please wait a moment."));
            }

            // Look up by tracking code — only expose PHYSICAL letters
            DakpionLetterEntity letter = letterRepo.findByTrackingCode(trackingCode)
                    .filter(l -> l.getDeliveryType() == DeliveryType.PHYSICAL)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No physical letter found with tracking code: " + trackingCode));

            List<DakpionDeliveryEventEntity> events = deliveryEventRepo
                    .findByLetterIdOrderByOccurredAtAsc(letter.getId());

            String currentStatus = events.isEmpty() ? "SUBMITTED"
                    : events.get(events.size() - 1).getStatus().name();

            PublicTrackingDto dto = PublicTrackingDto.builder()
                    .trackingCode(trackingCode)
                    .currentStatus(currentStatus)
                    .estimatedDelivery(null)
                    .events(events.stream()
                            .map(e -> PublicTrackingDto.DeliveryEventDto.builder()
                                    .status(e.getStatus().name())
                                    .note(e.getNote())
                                    .occurredAt(e.getOccurredAt())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            log.debug("[Tracking] Served tracking for {} to IP {}", trackingCode, maskIp(clientIp));
            return baseUtils.generateSuccessResponse(dto, PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    private boolean checkRateLimit(String ip) {
        long now = System.currentTimeMillis();
        RateLimitEntry entry = ipRateLimits.compute(ip, (k, existing) -> {
            if (existing == null || (now - existing.windowStart) > WINDOW_MILLIS) {
                return new RateLimitEntry(now);
            }
            existing.count.incrementAndGet();
            return existing;
        });

        // Cleanup stale entries occasionally if map grows
        if (ipRateLimits.size() > 5000) {
            ipRateLimits.entrySet().removeIf(e -> (now - e.getValue().windowStart) > WINDOW_MILLIS * 2);
        }

        return entry.count.get() <= MAX_REQUESTS_PER_MIN;
    }

    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String maskIp(String ip) {
        if (ip == null) return "?";
        int last = ip.lastIndexOf('.');
        return (last > 0) ? ip.substring(0, last) + ".***" : "***";
    }
}
