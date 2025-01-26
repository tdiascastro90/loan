package com.creditas.loan.error_util;

import org.springframework.http.HttpStatus;

public class ValidateException extends RuntimeException {
    public ValidateException(String message) {
        super(message);
    }
}
