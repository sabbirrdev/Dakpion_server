package com.company.dakpion.dakpion.gateway.sms.exception;

public class SmsAuthException extends SmsGatewayException {
    public SmsAuthException(Integer errorCode, String message) {
        super(errorCode, message);
    }
}
