package com.company.dakpion.dakpion.exception;

import org.springframework.http.HttpStatus;

public class RateLimitExceededException extends DakpionException {
    public RateLimitExceededException(String message) {
        super("RATE_LIMITED", message, HttpStatus.TOO_MANY_REQUESTS);
    }
}
