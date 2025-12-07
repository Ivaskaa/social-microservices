package com.example.authservice.logic.auth.controller;

import com.example.authservice.logic.auth.model.request.LoginRequest;
import com.example.authservice.logic.auth.model.request.RefreshTokenRequest;
import com.example.authservice.logic.auth.model.request.RegisterRequest;
import com.example.authservice.logic.auth.model.response.TokenResponse;
import com.example.authservice.logic.auth.service.JwtService;
import com.example.authservice.logic.auth.service.RefreshTokenService;
import com.example.authservice.logic.auth.validator.AuthValidator;
import com.example.authservice.logic.user.service.UserService;
import com.example.authservice.utils.errors_validation.ErrorUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final AuthValidator authValidator;

    @PostMapping("/login")
    public TokenResponse login(
            @Valid @RequestBody LoginRequest request,
            BindingResult bindingResult
    ) {
        ErrorUtils.validate(bindingResult);
        authValidator.validateLogin(request);

        String accessToken = jwtService.generateAccessToken(request.getLogin());
        String refreshToken = refreshTokenService.createAndStore(request.getLogin());
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @PostMapping("/registration")
    public TokenResponse register(
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult
    ) {
        ErrorUtils.validate(bindingResult);
        authValidator.validateRegistration(request);

        userService.registration(request);
        String accessToken = jwtService.generateAccessToken(request.getLogin());
        String refreshToken = refreshTokenService.createAndStore(request.getLogin());
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(
            @Valid @RequestBody RefreshTokenRequest request,
            BindingResult bindingResult
    ) {
        ErrorUtils.validate(bindingResult);

        String subject = refreshTokenService.consumeRefreshToken(request.getRefreshToken());
        String accessToken = jwtService.generateAccessToken(subject);
        String newRefreshToken = refreshTokenService.createAndStore(subject);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
