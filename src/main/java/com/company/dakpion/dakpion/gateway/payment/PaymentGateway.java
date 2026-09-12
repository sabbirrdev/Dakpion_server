package com.company.dakpion.dakpion.gateway.payment;

import com.company.dakpion.dakpion.constant.PaymentMethod;
import com.company.dakpion.dakpion.dto.PaymentInitiateRequestDto;
import com.company.dakpion.dakpion.dto.PaymentInitiateResponseDto;
import com.company.dakpion.dakpion.dto.PaymentWebhookPayloadDto;

public interface PaymentGateway {
    PaymentMethod getProvider();
    PaymentInitiateResponseDto initiate(PaymentInitiateRequestDto request, String transactionId);
    boolean verifyWebhook(PaymentWebhookPayloadDto payload);
}
