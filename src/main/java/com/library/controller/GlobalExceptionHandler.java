package com.library.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

/**
 * Global exception handler — catches unhandled exceptions and returns
 * structured JSON error responses instead of Spring's default HTML error page.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Catch-all for unexpected runtime errors */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "An unexpected server error occurred.",
                        "details", ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName()
                ));
    }

    /** 404 for unknown routes */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Endpoint not found: " + ex.getMessage()));
    }

    /** Bad / malformed JSON in request body */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleBadJson(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Malformed JSON request body."));
    }
}
