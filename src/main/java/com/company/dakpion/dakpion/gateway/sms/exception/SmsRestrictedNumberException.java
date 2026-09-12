package com.company.dakpion.dakpion.gateway.sms.exception;

public class SmsRestrictedNumberException extends SmsGatewayException {
    public SmsRestrictedNumberException(Integer errorCode, String message) {
        super(errorCode, message);
    }
}
