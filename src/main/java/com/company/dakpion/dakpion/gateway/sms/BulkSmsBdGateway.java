package com.company.dakpion.dakpion.gateway.sms;

import com.company.dakpion.dakpion.config.DakpionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "dakpion.sms.provider", havingValue = "bulksmsbd")
public class BulkSmsBdGateway implements SmsGateway {

    private final DakpionProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean sendSms(String recipientPhone, String message) {
        try {
            String apiKey = properties.getSms().getApiKey();
            String senderId = properties.getSms().getSenderId();
            String endpoint = properties.getSms().getApiEndpoint() != null 
                    ? properties.getSms().getApiEndpoint() 
                    : "http://bulksmsbd.net/api/smsapi";

            String encodedMsg = URLEncoder.encode(message, StandardCharsets.UTF_8);
            String url = String.format("%s?api_key=%s&type=text&number=%s&senderid=%s&message=%s",
                    endpoint, apiKey, recipientPhone, senderId, encodedMsg);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            log.info("[BulkSMSBD] Response status: {}", response.getStatusCode());
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("[BulkSMSBD] Failed to send SMS to {}: {}", recipientPhone, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getProviderName() {
        return "BULK_SMS_BD";
    }
}
