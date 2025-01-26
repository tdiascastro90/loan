package com.creditas.loan.error_util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ValidateException.class)
    public ResponseEntity<String> handleValidateException(ValidateException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
