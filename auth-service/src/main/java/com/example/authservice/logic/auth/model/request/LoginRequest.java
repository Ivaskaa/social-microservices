package com.example.authservice.logic.auth.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {
    @Email
    @NotNull
    @NotBlank
    private String login;
    @NotNull
    @NotBlank
    private String password;
}
