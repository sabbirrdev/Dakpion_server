package com.company.dakpion.dakpion.service.impl;
import com.company.dakpion.config.SSLCommerzConfig;
import com.company.dakpion.dakpion.constant.PaymentMethod;
import com.company.dakpion.dakpion.constant.PaymentStatus;
import com.company.dakpion.dakpion.dto.PaymentDto;
import com.company.dakpion.dakpion.dto.SSLPaymentRequestDto;
import com.company.dakpion.dakpion.entity.DakpionLetterEntity;
import com.company.dakpion.dakpion.entity.DakpionPaymentTransactionEntity;
import com.company.dakpion.dakpion.repository.DakpionLetterRepo;
import com.company.dakpion.dakpion.repository.PaymentTransactionRepo;
import com.company.dakpion.dakpion.service.PaymentService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final SSLCommerzConfig config;
    private final PaymentTransactionRepo paymentRepo;
    private final RestTemplate restTemplate = new RestTemplate();
    private final DakpionLetterRepo dakpionLetterRepo;

    @Transactional
    @Override
    public String initiatePayment(PaymentDto paymentDto, Long userId) {
        DakpionLetterEntity order = dakpionLetterRepo.findById(paymentDto.getOrderId()).orElseThrow(() -> new RuntimeException("Order not found"));
        PaymentMethod paymentMethod = resolvePaymentMethod(paymentDto.getPaymentMethod());

        DakpionPaymentTransactionEntity payment = paymentRepo.findFirstByLetterIdOrderByCreatedAtDesc(order.getId()).orElse(new DakpionPaymentTransactionEntity());
        payment.setLetter(order);
        payment.setTransactionId(paymentMethod == PaymentMethod.COD ? "COD-" + order.getId() + "-" + System.currentTimeMillis() : "TXN" + System.currentTimeMillis());
        payment.setStatus(PaymentStatus.UNPAID);
        payment.setAmount(order.getTotalAmount());
        payment.setProvider(paymentMethod);
        payment.setCreatedAt(LocalDateTime.now());
        //payment.setEntryUser(userId);
        paymentRepo.save(payment);

        Logger logger = Logger.getGlobal();
        logger.info("PAYMENT DTO " + paymentDto.getOrderId());
        logger.info("CUSTOMER NAME " + (order.getSenderNickname() != null ? order.getSenderNickname(): "Customer"));
        SSLPaymentRequestDto request = new SSLPaymentRequestDto();
        request.setTotal_amount(String.valueOf(order.getTotalAmount()));
        request.setTran_id(payment.getTransactionId());
        request.setSuccess_url(resolveCallbackUrl(config.getSuccessUrl(), "/api/v1/payments/success"));
        request.setFail_url(resolveCallbackUrl(config.getFailUrl(), "/api/v1/payments/fail"));
        request.setCancel_url(resolveCallbackUrl(config.getCancelUrl(), "/api/v1/payments/cancel"));
        request.setIpn_url(resolveCallbackUrl(config.getSuccessUrl(), "/api/v1/payments/success"));
        request.setCus_name(order.getSenderNickname() != null ? order.getSenderNickname() : "Customer");
        request.setCus_phone(order.getRecipientPhone() != null ? order.getRecipientPhone() : "");
       // request.setCus_email(order.getCustomer() != null ? order.getCustomer().getEmail() : "");
        request.setCus_add1(order.getShippingAddress() != null ? order.getShippingAddress() : "");
//        request.setCus_city(order.getDeliveryAddress() != null ? order.getDeliveryAddress().getDistrict() : "");
//        request.setCus_postcode(order.getDeliveryAddress() != null && order.getDeliveryAddress().getPostCode() != null ? order.getDeliveryAddress().getPostCode().toString() : "");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.convertValue(request, new TypeReference<>() {});
        body.setAll(map);
        body.add("store_id", config.getStoreId());
        body.add("store_passwd", config.getStorePassword());

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(config.getInitUrl(), httpEntity, Map.class);

        logger.info("SSLCommerz Response: " + response.getBody());

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Object gatewayUrl = response.getBody().get("GatewayPageURL");
            return gatewayUrl != null ? gatewayUrl.toString() : "SSLCommerz payment initialized";
        } else {
            throw new RuntimeException("Failed to create SSLCommerz Payment");
        }
    }

    @Transactional
    @Override
    public void handleSuccess(Map<String, String> params) {
        Logger.getGlobal().info("params: " + params);
        String txnId = params.get("tran_id");
        String status = params.get("status");
        String valId = params.get("val_id");

        if (txnId == null || txnId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID is missing");
        }

        // Validate status from gateway callback
        if (status == null || (!status.equalsIgnoreCase("VALID") && !status.equalsIgnoreCase("VALIDATED"))) {
            throw new SecurityException("Payment validation failed: status is " + status);
        }

        Logger.getGlobal().info("Payment Success Verified: " + txnId + " with status: " + status);

        DakpionPaymentTransactionEntity payment = paymentRepo.findByTransactionId(txnId)
                .orElseThrow(() -> new RuntimeException("Payment not found for transaction: " + txnId));

        // Validate amount match if provided
        String callbackAmount = params.get("amount");
        if (callbackAmount != null && !callbackAmount.isBlank() && payment.getAmount() != null) {
            try {
                double paidAmount = Double.parseDouble(callbackAmount.trim());
                if (Math.abs(paidAmount - payment.getAmount().doubleValue()) > 0.01) {
                    throw new SecurityException("Payment amount mismatch. Expected: " + payment.getAmount() + ", received: " + paidAmount);
                }
            } catch (NumberFormatException ignored) {}
        }

        payment.setStatus(PaymentStatus.PAID);
        paymentRepo.save(payment);

        DakpionLetterEntity order = payment.getLetter();
        if (order != null) {
            order.setPaymentStatus(PaymentStatus.PAID);
            dakpionLetterRepo.save(order);
        }
    }

    private PaymentMethod resolvePaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isBlank()) {
            return PaymentMethod.COD;
        }

        String normalized = paymentMethod.trim().toUpperCase();
        return switch (normalized) {
            case "COD" -> PaymentMethod.COD;
            case "CARD" -> PaymentMethod.CARD;
            case "BKASH" -> PaymentMethod.BKASH;
            case "NAGAD" -> PaymentMethod.NAGAD;
            case "SSLCOMMERZ", "SSL", "SSL_COMMERZ" -> PaymentMethod.SSLCOMMERZ;
            default -> PaymentMethod.COD;
        };
    }

    private String resolveCallbackUrl(String configuredUrl, String fallbackPath) {
        if (configuredUrl != null && !configuredUrl.isBlank()) {
            return configuredUrl;
        }
        return "http://localhost:8080" + fallbackPath;
    }
}
