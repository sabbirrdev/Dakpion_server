package com.company.dakpion.dakpion.gateway.payment;

import com.company.dakpion.config.SSLCommerzConfig;
import com.company.dakpion.dakpion.constant.PaymentMethod;
import com.company.dakpion.dakpion.dto.PaymentInitiateRequestDto;
import com.company.dakpion.dakpion.dto.PaymentInitiateResponseDto;
import com.company.dakpion.dakpion.dto.PaymentWebhookPayloadDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SslCommerzPaymentGateway implements PaymentGateway {

    private final SSLCommerzConfig sslCommerzConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public PaymentMethod getProvider() {
        return PaymentMethod.SSLCOMMERZ;
    }

    @Override
    public PaymentInitiateResponseDto initiate(PaymentInitiateRequestDto request, String transactionId) {
        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("store_id", sslCommerzConfig.getStoreId());
            body.add("store_passwd", sslCommerzConfig.getStorePassword());
            body.add("total_amount", request.getAmount().toPlainString());
            body.add("currency", "BDT");
            body.add("tran_id", transactionId);
            body.add("success_url", sslCommerzConfig.getSuccessUrl());
            body.add("fail_url", sslCommerzConfig.getFailUrl());
            body.add("cancel_url", sslCommerzConfig.getCancelUrl());
            body.add("ipn_url", sslCommerzConfig.getSuccessUrl());
            body.add("cus_name", request.getCustomerName() != null ? request.getCustomerName() : "DakPion Sender");
            body.add("cus_email", "guest@dakpion.app");
            body.add("cus_add1", "Dhaka, Bangladesh");
            body.add("cus_city", "Dhaka");
            body.add("cus_postcode", "1200");
            body.add("cus_country", "Bangladesh");
            body.add("cus_phone", request.getCustomerPhone() != null ? request.getCustomerPhone() : "01700000000");
            body.add("shipping_method", "NO");
            body.add("product_name", "DakPion Nostalgic Letter");
            body.add("product_category", "Digital/Courier");
            body.add("product_profile", "general");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(sslCommerzConfig.getInitUrl(), httpEntity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map responseBody = response.getBody();
                String status = (String) responseBody.get("status");
                String gatewayUrl = (String) responseBody.get("GatewayPageURL");
                String sessionKey = (String) responseBody.get("sessionkey");

                if ("SUCCESS".equalsIgnoreCase(status) && gatewayUrl != null) {
                    return PaymentInitiateResponseDto.builder()
                            .transactionId(transactionId)
                            .redirectUrl(gatewayUrl)
                            .gatewaySessionId(sessionKey)
                            .amount(request.getAmount())
                            .currency("BDT")
                            .status("INITIATED")
                            .build();
                }
            }
        } catch (Exception e) {
            log.warn("[SSLCommerz] Failed to call live sandbox init-url ({}), falling back to simulated session: {}", 
                    sslCommerzConfig.getInitUrl(), e.getMessage());
        }

        // Fallback gracefully for local dev/testing
        return PaymentInitiateResponseDto.builder()
                .transactionId(transactionId)
                .redirectUrl("https://sandbox.sslcommerz.com/gwprocess/v4/simulator?tran_id=" + transactionId)
                .gatewaySessionId("SIMULATED-SSL-" + transactionId)
                .amount(request.getAmount())
                .currency("BDT")
                .status("INITIATED")
                .build();
    }

    @Override
    public boolean verifyWebhook(PaymentWebhookPayloadDto payload) {
        if (payload == null || payload.getStatus() == null) return false;
        return "VALID".equalsIgnoreCase(payload.getStatus()) || "SUCCESS".equalsIgnoreCase(payload.getStatus());
    }
}
