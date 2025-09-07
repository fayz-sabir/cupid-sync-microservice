package com.nuitee.webapplication.controller.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.validation.ConstraintViolationException;

import java.time.Instant;
import java.util.Map;

import com.nuitee.domain.exception.CupidNotFoundException;
import com.nuitee.domain.exception.ValidationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CupidNotFoundException.class)
    public ResponseEntity<?> handleNotFound(CupidNotFoundException ex) {
        LOGGER.error("Cupid not found: {}", ex.getMessage(), ex);
        return body(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class,
                       ConstraintViolationException.class, BindException.class})
    public ResponseEntity<?> handleValidation(Exception ex) {
        LOGGER.error("Validation error: {}", ex.getMessage(), ex);
        return body(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        LOGGER.error("An unhandled exception occurred: {}", ex.getMessage(), ex);
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> body(HttpStatus status, String code, String detail) {
        return ResponseEntity.status(status).body(Map.of(
            "timestamp", Instant.now().toString(),
            "status", status.value(),
            "error", code,
            "message", detail
        ));
    }
}
