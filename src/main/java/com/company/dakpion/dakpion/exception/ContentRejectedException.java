package com.company.dakpion.dakpion.exception;

import org.springframework.http.HttpStatus;

public class ContentRejectedException extends DakpionException {
    public ContentRejectedException(String message) {
        super("CONTENT_REJECTED", message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
