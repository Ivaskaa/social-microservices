package com.example.authservice.logic.auth.controller;

import com.example.authservice.logic.auth.model.request.LoginRequest;
import com.example.authservice.logic.auth.model.request.RefreshTokenRequest;
import com.example.authservice.logic.auth.model.request.RegisterRequest;
import com.example.authservice.logic.auth.model.response.TokenResponse;
import com.example.authservice.logic.auth.service.AuthService;
import com.example.authservice.logic.auth.validator.AuthValidator;
import com.example.authservice.utils.errors_validation.ErrorUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthValidator authValidator;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request,
            BindingResult bindingResult
    ) {
        ErrorUtils.validate(bindingResult);
        authValidator.validateLogin(request);

        TokenResponse response = authService.login(request.getLogin());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registration")
    public ResponseEntity<TokenResponse> register(
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult
    ) {
        authValidator.validateRegistration(request, bindingResult);
        ErrorUtils.validate(bindingResult);

        TokenResponse response = authService.registration(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            BindingResult bindingResult
    ) {
        ErrorUtils.validate(bindingResult);

        TokenResponse response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}
