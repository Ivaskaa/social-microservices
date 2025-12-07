package com.example.authservice.utils.swagger_custom_error;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomApiErrors {
    CustomApiError[] value();
}