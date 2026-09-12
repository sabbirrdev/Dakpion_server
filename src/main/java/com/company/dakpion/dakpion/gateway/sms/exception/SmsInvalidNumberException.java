package com.company.dakpion.dakpion.gateway.sms.exception;

public class SmsInvalidNumberException extends SmsGatewayException {
    public SmsInvalidNumberException(Integer errorCode, String message) {
        super(errorCode, message);
    }
}
