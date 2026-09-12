package com.company.dakpion.config;

import com.company.dakpion.base.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String GENERIC_DB_ERROR_MSG = "A secure database communication error occurred. Please try again later.";
    private static final String GENERIC_SYSTEM_ERROR_MSG = "An internal security/system error occurred. Please try again later.";



    /**
     * Handles database transaction creation failure (e.g. database unreachable or pool exhausted).
     */
    @ExceptionHandler(CannotCreateTransactionException.class)
    public ResponseEntity<ApiErrorResponse> handleCannotCreateTransaction(
            CannotCreateTransactionException ex, HttpServletRequest request) {

        String correlationId = resolveCorrelationId(request);
        log.error("[CorrelationID: {}] Database Transaction Failure on URI {}: {}",
                correlationId, request.getRequestURI(), ex.getMessage(), ex);

        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message("Service is temporarily unable to process transactions. Please try again shortly.")
                .correlationId(correlationId)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * Handles database constraint and integrity violations (e.g. duplicate key, foreign key errors)
     * without leaking table names, SQL statements, or database internal details.
     */
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException ex, HttpServletRequest request) {

        String correlationId = resolveCorrelationId(request);
        log.error("[CorrelationID: {}] Data Integrity Violation on URI {}: {}",
                correlationId, request.getRequestURI(), ex.getMessage(), ex);

        String friendlyMessage = "The operation could not be completed because a conflicting record already exists or required references are missing.";
        String rootMsg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "";
        if (rootMsg != null && rootMsg.toLowerCase().contains("duplicate")) {
            friendlyMessage = "A record with this identifier or unique value already exists.";
        }

        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .statusCode(HttpStatus.CONFLICT.value())
                .message(friendlyMessage)
                .correlationId(correlationId)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Handles Spring Data Access exceptions (Hibernate mapping, missing columns/tables).
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleDataAccessException(
            DataAccessException ex, HttpServletRequest request) {

        String correlationId = resolveCorrelationId(request);
        log.error("[CorrelationID: {}] Data Access Exception on URI {}: {}",
                correlationId, request.getRequestURI(), ex.getMessage(), ex);

        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(GENERIC_DB_ERROR_MSG)
                .correlationId(correlationId)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handles raw JDBC SQLExceptions.
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiErrorResponse> handleSQLException(
            SQLException ex, HttpServletRequest request) {

        String correlationId = resolveCorrelationId(request);
        log.error("[CorrelationID: {}] SQL Exception [ErrorCode: {}, SQLState: {}] on URI {}: {}",
                correlationId, ex.getErrorCode(), ex.getSQLState(), request.getRequestURI(), ex.getMessage(), ex);

        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(GENERIC_DB_ERROR_MSG)
                .correlationId(correlationId)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handles client validation errors (@Valid / @NotNull / @Min).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String correlationId = resolveCorrelationId(request);
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        log.warn("[CorrelationID: {}] Validation failed on URI {}: {}", correlationId, request.getRequestURI(), errors);

        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Request validation failed.")
                .validationErrors(errors)
                .correlationId(correlationId)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles specialized DakPion domain exceptions (e.g. RATE_LIMITED, OTP_INVALID, CONTENT_REJECTED, NOT_FOUND).
     */
    @ExceptionHandler(com.company.dakpion.dakpion.exception.DakpionException.class)
    public ResponseEntity<ApiErrorResponse> handleDakpionException(
            com.company.dakpion.dakpion.exception.DakpionException ex, HttpServletRequest request) {

        String correlationId = resolveCorrelationId(request);
        log.warn("[CorrelationID: {}] DakPion exception [{}] on URI {}: {}",
                correlationId, ex.getErrorCode(), request.getRequestURI(), ex.getMessage());

        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .code(ex.getErrorCode())
                .statusCode(ex.getHttpStatus().value())
                .message(ex.getMessage())
                .correlationId(correlationId)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    /**
     * Handles domain business rule violations.
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiErrorResponse> handleBusinessExceptions(
            RuntimeException ex, HttpServletRequest request) {

        String correlationId = resolveCorrelationId(request);
        log.warn("[CorrelationID: {}] Business logic exception on URI {}: {}",
                correlationId, request.getRequestURI(), ex.getMessage());

        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .code("BAD_REQUEST")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .correlationId(correlationId)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Fallback handler for any uncaught exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        String correlationId = resolveCorrelationId(request);
        log.error("[CorrelationID: {}] Unhandled Exception on URI {}: {}",
                correlationId, request.getRequestURI(), ex.getMessage(), ex);

        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(GENERIC_SYSTEM_ERROR_MSG)
                .correlationId(correlationId)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String resolveCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader("X-Correlation-ID");
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        return correlationId;
    }
}
