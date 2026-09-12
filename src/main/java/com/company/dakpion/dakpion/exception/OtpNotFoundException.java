package com.company.dakpion.dakpion.exception;

import org.springframework.http.HttpStatus;

public class OtpNotFoundException extends DakpionException {
    public OtpNotFoundException(String message) {
        super("OTP_NOT_FOUND", message, HttpStatus.BAD_REQUEST);
    }

    public OtpNotFoundException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.BAD_REQUEST);
    }
}
