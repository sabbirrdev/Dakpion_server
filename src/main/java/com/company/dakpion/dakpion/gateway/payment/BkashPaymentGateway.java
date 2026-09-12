package com.company.dakpion.dakpion.gateway.payment;

import com.company.dakpion.dakpion.constant.PaymentMethod;
import com.company.dakpion.dakpion.dto.PaymentInitiateRequestDto;
import com.company.dakpion.dakpion.dto.PaymentInitiateResponseDto;
import com.company.dakpion.dakpion.dto.PaymentWebhookPayloadDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BkashPaymentGateway implements PaymentGateway {

    @Override
    public PaymentMethod getProvider() {
        return PaymentMethod.BKASH;
    }

    @Override
    public PaymentInitiateResponseDto initiate(PaymentInitiateRequestDto request, String transactionId) {
        log.info("[bKash] Initiating bKash payment for txn: {}, amount: {}", transactionId, request.getAmount());
        return PaymentInitiateResponseDto.builder()
                .transactionId(transactionId)
                .redirectUrl("https://sandbox.bkash.com/payment?paymentID=" + transactionId)
                .gatewaySessionId("BKASH-" + transactionId)
                .amount(request.getAmount())
                .currency("BDT")
                .status("INITIATED")
                .build();
    }

    @Override
    public boolean verifyWebhook(PaymentWebhookPayloadDto payload) {
        return "SUCCESS".equalsIgnoreCase(payload.getStatus()) || "Completed".equalsIgnoreCase(payload.getStatus());
    }
}
