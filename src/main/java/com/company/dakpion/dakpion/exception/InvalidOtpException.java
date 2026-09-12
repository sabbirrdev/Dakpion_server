package com.company.dakpion.dakpion.exception;

import org.springframework.http.HttpStatus;

public class InvalidOtpException extends DakpionException {
    public InvalidOtpException(String message) {
        super("OTP_INVALID", message, HttpStatus.BAD_REQUEST);
    }

    public InvalidOtpException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.BAD_REQUEST);
    }
}
