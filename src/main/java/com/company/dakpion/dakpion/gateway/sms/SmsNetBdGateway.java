package com.company.dakpion.dakpion.gateway.sms;

import com.company.dakpion.dakpion.config.DakpionProperties;
import com.company.dakpion.dakpion.gateway.sms.dto.SmsBalanceReport;
import com.company.dakpion.dakpion.gateway.sms.dto.SmsDeliveryReport;
import com.company.dakpion.dakpion.gateway.sms.dto.SmsSendResult;
import com.company.dakpion.dakpion.gateway.sms.exception.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Real SMS gateway client for sms.net.bd.
 * Automatically selected when dakpion.sms.provider=sms-net-bd.
 *
 * IMPORTANT: The API key is NEVER logged or included in exception messages.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "dakpion.sms.provider", havingValue = "sms-net-bd")
public class SmsNetBdGateway implements SmsGateway {

    private static final String BASE_URL = "https://api.sms.net.bd";
    private static final String SEND_SMS_URL = BASE_URL + "/sendsms";
    private static final String REPORT_URL = BASE_URL + "/report/request/{requestId}/";
    private static final String BALANCE_URL = BASE_URL + "/user/balance/";

    // Bangladeshi phone number: 01XXXXXXXXX (11 digits) or 8801XXXXXXXXX (13 digits)
    private static final Pattern BD_PHONE_PATTERN = Pattern.compile("^(?:(?:\\+?88)?01[3-9]\\d{8})$");

    private final DakpionProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public boolean sendSms(String recipientPhone, String message) {
        SmsSendResult result = send(recipientPhone, message);
        return result.isSuccess();
    }

    @Override
    public String getProviderName() {
        return "sms-net-bd";
    }

    /**
     * Sends an SMS to the given phone number. Returns a SmsSendResult with the gateway request ID.
     */
    public SmsSendResult send(String recipientPhone, String message) {
        String normalizedPhone = normalizePhone(recipientPhone);
        log.info("[SmsNetBd] Sending SMS to {}", maskPhone(normalizedPhone));

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("api_key", getApiKey());
            params.add("msg", message);
            params.add("to", normalizedPhone);

            String senderId = properties.getSms().getSenderId();
            if (senderId != null && !senderId.isBlank()) {
                params.add("sender_id", senderId);
            }

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(SEND_SMS_URL, request, String.class);
            String body = response.getBody();
            log.debug("[SmsNetBd] Raw response for send: {}", body);

            return parseSendResponse(body, normalizedPhone);
        } catch (SmsGatewayException e) {
            throw e;
        } catch (Exception e) {
            log.error("[SmsNetBd] Unexpected error sending SMS to {}: {}", maskPhone(normalizedPhone), e.getMessage());
            return SmsSendResult.builder()
                    .success(false)
                    .errorMessage("Network or parsing error: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Fetches delivery report for a previously sent SMS request.
     */
    public SmsDeliveryReport getDeliveryReport(String requestId) {
        log.debug("[SmsNetBd] Fetching delivery report for request {}", requestId);
        try {
            String url = BASE_URL + "/report/request/" + requestId + "/?api_key=" + getApiKey();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String body = response.getBody();
            log.debug("[SmsNetBd] Raw delivery report response: {}", body);
            return parseReportResponse(body);
        } catch (SmsGatewayException e) {
            throw e;
        } catch (Exception e) {
            log.error("[SmsNetBd] Error fetching delivery report for {}: {}", requestId, e.getMessage());
            return SmsDeliveryReport.builder()
                    .requestId(requestId)
                    .errorMessage("Error fetching report: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Fetches account balance.
     */
    public SmsBalanceReport getBalance() {
        try {
            String url = BASE_URL + "/user/balance/?api_key=" + getApiKey();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String body = response.getBody();
            return parseBalanceResponse(body);
        } catch (SmsGatewayException e) {
            throw e;
        } catch (Exception e) {
            log.error("[SmsNetBd] Error fetching balance: {}", e.getMessage());
            return SmsBalanceReport.builder()
                    .errorMessage("Error fetching balance: " + e.getMessage())
                    .build();
        }
    }

    // -------------------------------------------------------------------------
    // Phone normalization
    // -------------------------------------------------------------------------

    public static String normalizePhone(String phone) {
        if (phone == null) throw new SmsInvalidNumberException(416, "Phone number is null");
        String clean = phone.replaceAll("\\s+", "");
        // Strip leading + if present
        if (clean.startsWith("+")) clean = clean.substring(1);
        // Strip leading 88 prefix to get bare 01XXXXXXXXX
        if (clean.startsWith("880") && clean.length() == 13) {
            clean = "0" + clean.substring(3);
        }
        if (!BD_PHONE_PATTERN.matcher("+" + "88" + clean).matches() &&
            !BD_PHONE_PATTERN.matcher(clean).matches()) {
            throw new SmsInvalidNumberException(416, "Invalid Bangladeshi phone format: " + maskPhone(clean));
        }
        // Normalize to 8801XXXXXXXXX format
        if (clean.startsWith("01") && clean.length() == 11) {
            return "880" + clean.substring(1);
        }
        if (clean.startsWith("8801") && clean.length() == 13) {
            return clean;
        }
        throw new SmsInvalidNumberException(416, "Cannot normalize phone number: " + maskPhone(clean));
    }

    // -------------------------------------------------------------------------
    // Response parsers
    // -------------------------------------------------------------------------

    private SmsSendResult parseSendResponse(String body, String phone) {
        try {
            JsonNode root = objectMapper.readTree(body);
            int error = root.path("error").asInt(0);
            String msg = root.path("msg").asText("");

            if (error != 0) {
                throwTypedSmsException(error, msg);
            }

            String requestId = null;
            JsonNode data = root.path("data");
            if (!data.isMissingNode()) {
                requestId = data.path("request_id").asText(null);
            }

            log.info("[SmsNetBd] SMS sent to {}. requestId={}", maskPhone(phone), requestId);
            return SmsSendResult.builder()
                    .success(true)
                    .requestId(requestId)
                    .build();
        } catch (SmsGatewayException e) {
            throw e;
        } catch (Exception e) {
            log.error("[SmsNetBd] Failed to parse send response: {}", e.getMessage());
            return SmsSendResult.builder()
                    .success(false)
                    .errorMessage("Failed to parse gateway response")
                    .build();
        }
    }

    private SmsDeliveryReport parseReportResponse(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            int error = root.path("error").asInt(0);
            String msg = root.path("msg").asText("");

            if (error != 0) {
                return SmsDeliveryReport.builder()
                        .errorCode(error)
                        .errorMessage(msg)
                        .build();
            }

            JsonNode data = root.path("data");
            String requestId = data.path("request_id").asText(null);
            String requestStatus = data.path("request_status").asText(null);

            List<SmsDeliveryReport.RecipientReport> recipients = new ArrayList<>();
            JsonNode recs = data.path("recipients");
            if (recs.isArray()) {
                for (JsonNode r : recs) {
                    BigDecimal charge = null;
                    JsonNode chargeNode = r.path("charge");
                    if (!chargeNode.isMissingNode()) {
                        try { charge = new BigDecimal(chargeNode.asText()); } catch (Exception ignored) {}
                    }
                    recipients.add(SmsDeliveryReport.RecipientReport.builder()
                            .number(r.path("number").asText(null))
                            .charge(charge)
                            .status(r.path("status").asText(null))
                            .build());
                }
            }

            return SmsDeliveryReport.builder()
                    .requestId(requestId)
                    .requestStatus(requestStatus)
                    .recipients(recipients)
                    .build();
        } catch (Exception e) {
            log.error("[SmsNetBd] Failed to parse delivery report: {}", e.getMessage());
            return SmsDeliveryReport.builder()
                    .errorMessage("Failed to parse delivery report: " + e.getMessage())
                    .build();
        }
    }

    private SmsBalanceReport parseBalanceResponse(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            int error = root.path("error").asInt(0);
            String msg = root.path("msg").asText("");

            if (error != 0) {
                throwTypedSmsException(error, msg);
            }

            JsonNode data = root.path("data");
            BigDecimal balance = null;
            try { balance = new BigDecimal(data.path("balance").asText("0")); } catch (Exception ignored) {}

            return SmsBalanceReport.builder().balance(balance).build();
        } catch (SmsGatewayException e) {
            throw e;
        } catch (Exception e) {
            log.error("[SmsNetBd] Failed to parse balance response: {}", e.getMessage());
            return SmsBalanceReport.builder()
                    .errorMessage("Failed to parse balance response: " + e.getMessage())
                    .build();
        }
    }

    // -------------------------------------------------------------------------
    // Error code mapping
    // -------------------------------------------------------------------------

    private void throwTypedSmsException(int errorCode, String message) {
        switch (errorCode) {
            case 400, 401, 402, 403, 404, 405, 409 ->
                    throw new SmsAuthException(errorCode, "SMS Auth error [" + errorCode + "]: " + message);
            case 410, 411 ->
                    throw new SmsAccountExpiredException(errorCode, "SMS Account expired [" + errorCode + "]: " + message);
            case 412, 413, 414, 415 ->
                    throw new SmsInvalidPayloadException(errorCode, "SMS Payload error [" + errorCode + "]: " + message);
            case 416 ->
                    throw new SmsInvalidNumberException(errorCode, "SMS Invalid number [" + errorCode + "]: " + message);
            case 417 ->
                    throw new SmsInsufficientBalanceException(errorCode, "SMS Insufficient balance [" + errorCode + "]: " + message);
            case 420 ->
                    throw new SmsContentBlockedException(errorCode, "SMS Content blocked [" + errorCode + "]: " + message);
            case 421 ->
                    throw new SmsRestrictedNumberException(errorCode, "SMS Restricted number [" + errorCode + "]: " + message);
            default ->
                    throw new SmsGatewayException(errorCode, "SMS Gateway error [" + errorCode + "]: " + message);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String getApiKey() {
        String key = properties.getSms().getApiKey();
        if (key == null || key.isBlank()) {
            throw new SmsGatewayException("SMS API key is not configured");
        }
        return key;
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 6) return "***";
        return phone.substring(0, 4) + "****" + phone.substring(phone.length() - 3);
    }
}
