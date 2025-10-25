package com.example.authservice.logic.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
