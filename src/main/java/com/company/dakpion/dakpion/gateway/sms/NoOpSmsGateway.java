package com.company.dakpion.dakpion.gateway.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "dakpion.sms.provider", havingValue = "noop", matchIfMissing = true)
public class NoOpSmsGateway implements SmsGateway {

    @Override
    public boolean sendSms(String recipientPhone, String message) {
        log.info("[SMS-NOOP] Simulated SMS sent to {}: {}", maskPhone(recipientPhone), message);
        return true;
    }

    @Override
    public String getProviderName() {
        return "NOOP_SIMULATOR";
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 6) return "***";
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 3);
    }
}
