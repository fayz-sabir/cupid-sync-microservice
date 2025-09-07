package com.nuitee.webapplication.controller.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.nuitee.domain.exception.CupidNotFoundException;
import com.nuitee.domain.exception.ValidationException;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFoundReturns404() {
        CupidNotFoundException ex = new CupidNotFoundException("missing");
        ResponseEntity<?> response = handler.handleNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("NOT_FOUND", body.get("error"));
        assertEquals("Cupid API error 404", body.get("message"));
    }

    @Test
    void handleValidationReturns400() {
        ValidationException ex = new ValidationException("bad");
        ResponseEntity<?> response = handler.handleValidation(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("VALIDATION_ERROR", body.get("error"));
        assertEquals("bad", body.get("message"));
    }

    @Test
    void handleGenericReturns500() {
        RuntimeException ex = new RuntimeException("boom");
        ResponseEntity<?> response = handler.handleGeneric(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("INTERNAL_ERROR", body.get("error"));
        assertEquals("boom", body.get("message"));
    }
}

