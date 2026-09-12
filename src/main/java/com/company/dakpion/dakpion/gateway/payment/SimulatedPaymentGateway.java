package com.company.dakpion.dakpion.gateway.payment;

import com.company.dakpion.dakpion.constant.PaymentMethod;
import com.company.dakpion.dakpion.dto.PaymentInitiateRequestDto;
import com.company.dakpion.dakpion.dto.PaymentInitiateResponseDto;
import com.company.dakpion.dakpion.dto.PaymentWebhookPayloadDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class SimulatedPaymentGateway implements PaymentGateway {

    @Override
    public PaymentMethod getProvider() {
        return PaymentMethod.SIMULATED;
    }

    @Override
    public PaymentInitiateResponseDto initiate(PaymentInitiateRequestDto request, String transactionId) {
        log.info("[SimulatedPayment] Initiating simulated payment txn: {}", transactionId);
        return PaymentInitiateResponseDto.builder()
                .transactionId(transactionId)
                .redirectUrl("/payments/simulate?tx=" + transactionId)
                .gatewaySessionId("SIM-" + UUID.randomUUID().toString().substring(0, 8))
                .amount(request.getAmount())
                .currency("BDT")
                .status("INITIATED")
                .build();
    }

    @Override
    public boolean verifyWebhook(PaymentWebhookPayloadDto payload) {
        return true;
    }
}
