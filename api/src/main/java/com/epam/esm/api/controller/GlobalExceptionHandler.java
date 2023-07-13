package com.epam.esm.api.controller;

import com.epam.esm.api.ErrorResponse;
import com.epam.esm.core.exception.ServiceException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.epam.esm.api.util.Constants.*;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        logger.error(e.getMessage());
        String errorMessage = e.getConstraintViolations().stream()
                .map(constraintViolation -> constraintViolation.getPropertyPath() + ": " + constraintViolation.getMessage())
                .collect(Collectors.joining("; "));

        ErrorResponse errorResponse = new ErrorResponse(errorMessage, ERROR_CONSTRAINT_VIOLATION);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        logger.error(e.getMessage());
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        logger.error(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "Data integrity violation: " + Objects.requireNonNull(e.getRootCause()).getMessage(),
                ERROR_CONSTRAINT_VIOLATION
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), ERROR_CONSTRAINT_VIOLATION);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public ResponseEntity<?> handleServiceException(ServiceException e) {
        logger.error(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), ERROR_INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.error(e.getMessage());
        String errorMessage = "Invalid request body: " + e.getMessage();
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, ERROR_METHOD_ARGUMENT_NOT_VALID);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    @ResponseBody
    public ResponseEntity<?> handleHttpClientErrorExceptionBadRequest(HttpClientErrorException.BadRequest e) {
        logger.error(e.getMessage());
        String errorMessage = e.getResponseBodyAsString();
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, ERROR_METHOD_ARGUMENT_NOT_VALID);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    @ResponseBody
    public ResponseEntity<?> handleHttpClientErrorExceptionForbidden(HttpClientErrorException.Forbidden e) {
        logger.error(e.getMessage());
        String errorMessage = e.getResponseBodyAsString();
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, ERROR_HTTP_CLIENT_ERROR_FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
}
