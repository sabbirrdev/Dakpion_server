package com.company.efood.websocket.service;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class LiveNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendSupportMessage(Long conversationId, Object payload) {
        messagingTemplate.convertAndSend("/topic/support/" + conversationId, payload);
    }

    public void sendOrderStatusUpdate(Long orderId, String status, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", orderId);
        payload.put("status", status);
        payload.put("message", message);
        messagingTemplate.convertAndSend("/topic/orders/" + orderId, payload);
    }

    public void sendRiderAssignment(Long orderId, Long raiderId, String vehicleType) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", orderId);
        payload.put("raiderId", raiderId);
        payload.put("vehicleType", vehicleType);
        payload.put("message", "Rider assigned for your order");
        messagingTemplate.convertAndSend("/topic/orders/" + orderId, payload);
    }

    public void sendRiderLocation(Long raiderId, Double lat, Double lng) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("raiderId", raiderId);
        payload.put("lat", lat);
        payload.put("lng", lng);
        payload.put("message", "Rider location updated");
        messagingTemplate.convertAndSend("/topic/raiders/" + raiderId + "/location", payload);
    }

    public void sendOrderTrackingUpdate(Long orderId, Map<String, Object> trackingData) {
        messagingTemplate.convertAndSend("/topic/orders/" + orderId + "/tracking", trackingData);
    }
}
