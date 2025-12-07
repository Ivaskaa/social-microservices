package com.example.authservice.utils.exceptions;

import com.example.authservice.utils.errors_validation.model.ValidationError;
import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {

    private final transient List<ValidationError> errors;

    public ValidationException(List<ValidationError> errors) {
        super("Validation failed");
        this.errors = errors;
    }

}
