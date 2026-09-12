package com.company.dakpion.dakpion.gateway.payment;

import com.company.dakpion.dakpion.constant.PaymentMethod;
import com.company.dakpion.dakpion.dto.PaymentInitiateRequestDto;
import com.company.dakpion.dakpion.dto.PaymentInitiateResponseDto;
import com.company.dakpion.dakpion.dto.PaymentWebhookPayloadDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NagadPaymentGateway implements PaymentGateway {

    @Override
    public PaymentMethod getProvider() {
        return PaymentMethod.NAGAD;
    }

    @Override
    public PaymentInitiateResponseDto initiate(PaymentInitiateRequestDto request, String transactionId) {
        log.info("[Nagad] Initiating Nagad payment for txn: {}, amount: {}", transactionId, request.getAmount());
        return PaymentInitiateResponseDto.builder()
                .transactionId(transactionId)
                .redirectUrl("https://sandbox.nagad.com.bd/payment?paymentID=" + transactionId)
                .gatewaySessionId("NAGAD-" + transactionId)
                .amount(request.getAmount())
                .currency("BDT")
                .status("INITIATED")
                .build();
    }

    @Override
    public boolean verifyWebhook(PaymentWebhookPayloadDto payload) {
        return "SUCCESS".equalsIgnoreCase(payload.getStatus());
    }
}
