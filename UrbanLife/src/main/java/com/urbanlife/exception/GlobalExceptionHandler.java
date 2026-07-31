package com.urbanlife.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.urbanlife.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==========================================
    // RESOURCE NOT FOUND - 404
    // ==========================================

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse>
            handleResourceNotFound(
                    ResourceNotFoundException ex,
                    HttpServletRequest request) {

        ErrorResponse errorResponse =
                buildErrorResponse(
                        HttpStatus.NOT_FOUND,
                        ex.getMessage(),
                        request.getRequestURI(),
                        null);

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.NOT_FOUND);
    }

    // ==========================================
    // VALIDATION ERRORS - 400
    // ==========================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>
            handleValidationException(
                    MethodArgumentNotValidException ex,
                    HttpServletRequest request) {

        Map<String, String> errors =
                new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                    errors.put(
                        error.getField(),
                        error.getDefaultMessage()));

        ErrorResponse errorResponse =
                buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        "Validation failed",
                        request.getRequestURI(),
                        errors);

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.BAD_REQUEST);
    }

    // ==========================================
    // ILLEGAL ARGUMENT - 400
    // ==========================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse>
            handleIllegalArgument(
                    IllegalArgumentException ex,
                    HttpServletRequest request) {

        ErrorResponse errorResponse =
                buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        ex.getMessage(),
                        request.getRequestURI(),
                        null);

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.BAD_REQUEST);
    }

    // ==========================================
    // ILLEGAL STATE (business rule violations) - 409
    // e.g. "User has already submitted a claim"
    //      "Only found items can be claimed"
    //      "Item is not available for claim"
    // ==========================================

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse>
            handleIllegalState(
                    IllegalStateException ex,
                    HttpServletRequest request) {

        ErrorResponse errorResponse =
                buildErrorResponse(
                        HttpStatus.CONFLICT,
                        ex.getMessage(),
                        request.getRequestURI(),
                        null);

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.CONFLICT);
    }


    // ==========================================
    // DATABASE CONSTRAINT ERRORS - 409
    // ==========================================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse>
            handleDataIntegrityViolation(
                    DataIntegrityViolationException ex,
                    HttpServletRequest request) {

        String rootMsg = ex.getRootCause() != null
            ? ex.getRootCause().getMessage().toLowerCase()
            : ex.getMessage().toLowerCase();

        String userMessage;
        if (rootMsg.contains("verification_reference")
                || rootMsg.contains("uk_domestic_staff_verification_ref")) {
            userMessage =
                "This Aadhaar / verification ID is already registered in the system. "
                + "Each domestic staff member must have a unique Aadhaar.";
        } else if (rootMsg.contains("email")) {
            userMessage = "An account with this email address already exists.";
        } else if (rootMsg.contains("phone")) {
            userMessage = "This phone number is already registered.";
        } else {
            userMessage = "A record with this data already exists. "
                + "Please check for duplicates.";
        }

        ErrorResponse errorResponse =
                buildErrorResponse(
                        HttpStatus.CONFLICT,
                        userMessage,
                        request.getRequestURI(),
                        null);

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.CONFLICT);
    }

    // ==========================================
    // ACCESS DENIED - 403
    // ==========================================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse>
            handleAccessDenied(
                    AccessDeniedException ex,
                    HttpServletRequest request) {

        ErrorResponse errorResponse =
                buildErrorResponse(
                        HttpStatus.FORBIDDEN,
                        "You do not have permission to access this resource",
                        request.getRequestURI(),
                        null);

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.FORBIDDEN);
    }

    // ==========================================
    // GENERAL SERVER ERROR - 500
    // ==========================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>
            handleGeneralException(
                    Exception ex,
                    HttpServletRequest request) {

        // Log the full stack trace so it's visible in Spring Boot console
        ex.printStackTrace();

        ErrorResponse errorResponse =
                buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred: " + ex.getMessage(),
                        request.getRequestURI(),
                        null);

        return new ResponseEntity<>(
                errorResponse,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ==========================================
    // COMMON ERROR RESPONSE BUILDER
    // ==========================================

    private ErrorResponse buildErrorResponse(
            HttpStatus status,
            String message,
            String path,
            Map<String, String> validationErrors) {

        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                validationErrors);
    }
}