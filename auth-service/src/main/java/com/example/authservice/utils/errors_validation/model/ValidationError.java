package com.example.authservice.utils.errors_validation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ValidationError {
    private String field;
    private String message;
    private Object data;
}
