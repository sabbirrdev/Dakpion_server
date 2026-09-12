package com.company.dakpion.dakpion.gateway.sms.exception;

public class SmsInsufficientBalanceException extends SmsGatewayException {
    public SmsInsufficientBalanceException(Integer errorCode, String message) {
        super(errorCode, message);
    }
}
