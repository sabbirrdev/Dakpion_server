package com.company.dakpion.dakpion.gateway.sms;

public interface SmsGateway {
    boolean sendSms(String recipientPhone, String message);
    String getProviderName();
}
