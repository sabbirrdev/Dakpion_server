package com.company.dakpion.dakpion.gateway.sms.exception;

public class SmsContentBlockedException extends SmsGatewayException {
    public SmsContentBlockedException(Integer errorCode, String message) {
        super(errorCode, message);
    }
}
