package com.company.dakpion.dakpion.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends DakpionException {
    public ResourceNotFoundException(String message) {
        super("NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }
}
