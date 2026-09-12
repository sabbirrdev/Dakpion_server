package com.company.dakpion.dakpion.exception;

import org.springframework.http.HttpStatus;

public class OtpExpiredException extends DakpionException {
    public OtpExpiredException(String message) {
        super("OTP_EXPIRED", message, HttpStatus.BAD_REQUEST);
    }
}
