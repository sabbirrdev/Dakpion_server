package com.company.dakpion.dakpion.gateway.sms.exception;

public class SmsInvalidPayloadException extends SmsGatewayException {
    public SmsInvalidPayloadException(Integer errorCode, String message) {
        super(errorCode, message);
    }
}
