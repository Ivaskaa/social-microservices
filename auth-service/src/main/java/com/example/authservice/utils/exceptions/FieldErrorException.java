package com.example.authservice.utils.exceptions;

import lombok.Getter;

@Getter
public class FieldErrorException extends RuntimeException {

    private final String field;
    private final String message;

    public FieldErrorException(String field, String message) {
        super(message);
        this.field = field;
        this.message = message;
    }

}
