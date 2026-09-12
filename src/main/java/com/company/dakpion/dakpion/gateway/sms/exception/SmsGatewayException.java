package com.company.dakpion.dakpion.gateway.sms.exception;

import lombok.Getter;

@Getter
public class SmsGatewayException extends RuntimeException {
    private final Integer errorCode;

    public SmsGatewayException(String message) {
        super(message);
        this.errorCode = null;
    }

    public SmsGatewayException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public SmsGatewayException(Integer errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
