package com.company.dakpion.dakpion.controller;

import com.company.dakpion.dakpion.constant.DeliveryEventStatus;
import com.company.dakpion.dakpion.entity.DakpionDeliveryEventEntity;
import com.company.dakpion.dakpion.entity.DakpionLetterEntity;
import com.company.dakpion.dakpion.repository.DakpionDeliveryEventRepo;
import com.company.dakpion.dakpion.repository.DakpionLetterRepo;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Receives courier webhook events from external providers (e.g. SteadFast, Pathao).
 * Appends a delivery event to the letter's tracking timeline.
 *
 * Security: Signature verification is per-provider.
 * Note: This endpoint is intentionally NOT behind JWT auth since couriers call it externally.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks/courier")
@RequiredArgsConstructor
public class DakpionCourierWebhookController {

    private final DakpionLetterRepo letterRepo;
    private final DakpionDeliveryEventRepo deliveryEventRepo;

    /**
     * POST /api/v1/webhooks/courier/{provider}
     * Receives webhook payload from courier provider.
     * Expects JSON body with at minimum: "tracking_code" or "consignment_id" and "status".
     */
    @PostMapping("/{provider}")
    public ResponseEntity<Void> receiveWebhook(
            @PathVariable String provider,
            @RequestBody JsonNode payload,
            @RequestHeader(value = "X-Signature", required = false) String signature) {

        log.info("[CourierWebhook] Received from provider={}. Verifying signature...", provider);

        // Signature verification (provider-specific)
        if (!verifySignature(provider, payload, signature)) {
            log.warn("[CourierWebhook] Signature verification FAILED for provider={}", provider);
            return ResponseEntity.status(401).build();
        }

        // Extract tracking code / consignment ID
        String courierTrackingId = extractTrackingId(provider, payload);
        if (courierTrackingId == null || courierTrackingId.isBlank()) {
            log.warn("[CourierWebhook] No tracking ID in payload from provider={}", provider);
            return ResponseEntity.badRequest().build();
        }

        // Extract status
        String rawStatus = extractStatus(provider, payload);
        DeliveryEventStatus eventStatus = mapCourierStatus(rawStatus);

        // Find matching letter by courier tracking ID
        Optional<DakpionLetterEntity> letterOpt = letterRepo.findByCourierTrackingId(courierTrackingId);
        if (letterOpt.isEmpty()) {
            log.warn("[CourierWebhook] No letter found for courierTrackingId={}", courierTrackingId);
            // Return 200 to prevent courier retry floods; just ignore unknown IDs
            return ResponseEntity.ok().build();
        }

        DakpionLetterEntity letter = letterOpt.get();

        // Append delivery event
        DakpionDeliveryEventEntity event = DakpionDeliveryEventEntity.builder()
                .letterId(letter.getId())
                .status(eventStatus)
                .note("Courier update from " + provider + ": " + rawStatus)
                .occurredAt(LocalDateTime.now())
                .createdBy("COURIER_WEBHOOK")
                .build();
        deliveryEventRepo.save(event);

        // Update letter courier status
        letter.setCourierStatus(rawStatus);
        letterRepo.save(letter);

        log.info("[CourierWebhook] Appended {} event for letter {} (provider={})",
                eventStatus, letter.getId(), provider);
        return ResponseEntity.ok().build();
    }

    // -------------------------------------------------------------------------
    // Provider-specific extraction and signature verification
    // -------------------------------------------------------------------------

    private boolean verifySignature(String provider, JsonNode payload, String signature) {
        // For now: SteadFast and Pathao don't mandate signature on standard webhooks.
        // In production, implement HMAC-SHA256 signature check per provider docs.
        // Return true here as a permissive default; replace with real check per contract.
        if ("pathao".equalsIgnoreCase(provider)) {
            // Pathao: Check X-Signature = HMAC-SHA256(secret, body)
            // TODO: inject pathao webhook secret and verify
            return true;
        }
        if ("steadfast".equalsIgnoreCase(provider)) {
            // SteadFast: Check api_key in header or body
            // TODO: inject steadfast api_key and verify
            return true;
        }
        // Unknown provider: accept but log
        log.warn("[CourierWebhook] Unknown provider={}, accepting without signature check", provider);
        return true;
    }

    private String extractTrackingId(String provider, JsonNode payload) {
        if ("steadfast".equalsIgnoreCase(provider)) {
            return payload.path("consignment_id").asText(null);
        }
        if ("pathao".equalsIgnoreCase(provider)) {
            return payload.path("order_id").asText(null);
        }
        // Generic fallback
        String id = payload.path("tracking_code").asText(null);
        if (id == null) id = payload.path("consignment_id").asText(null);
        if (id == null) id = payload.path("order_id").asText(null);
        return id;
    }

    private String extractStatus(String provider, JsonNode payload) {
        if ("steadfast".equalsIgnoreCase(provider)) {
            return payload.path("delivery_status").asText("UNKNOWN");
        }
        if ("pathao".equalsIgnoreCase(provider)) {
            return payload.path("order_status").asText("UNKNOWN");
        }
        return payload.path("status").asText("UNKNOWN");
    }

    private DeliveryEventStatus mapCourierStatus(String status) {
        if (status == null) return DeliveryEventStatus.IN_TRANSIT;
        return switch (status.toLowerCase()) {
            case "delivered", "completed" -> DeliveryEventStatus.DELIVERED;
            case "returned", "return" -> DeliveryEventStatus.RETURNED;
            case "partial_delivered" -> DeliveryEventStatus.DELIVERY_FAILED;
            case "out_for_delivery", "with_courier" -> DeliveryEventStatus.OUT_FOR_DELIVERY;
            default -> DeliveryEventStatus.IN_TRANSIT;
        };
    }
}
