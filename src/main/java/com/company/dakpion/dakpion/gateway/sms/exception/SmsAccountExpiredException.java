package com.company.dakpion.dakpion.gateway.sms.exception;

public class SmsAccountExpiredException extends SmsGatewayException {
    public SmsAccountExpiredException(Integer errorCode, String message) {
        super(errorCode, message);
    }
}
