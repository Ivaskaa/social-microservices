package com.example.authservice.utils.swagger_custom_error;

import com.example.authservice.utils.errors_validation.model.ValidationError;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(CustomApiErrors.class)
public @interface CustomApiError {
    String code() default "460";
    String description() default "Custom error";
    Class<?> schema() default ValidationError.class;
}