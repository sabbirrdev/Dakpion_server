package com.company.dakpion.dakpion.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DakpionException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus httpStatus;

    public DakpionException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public DakpionException(String errorCode, String message) {
        this(errorCode, message, HttpStatus.BAD_REQUEST);
    }
}
