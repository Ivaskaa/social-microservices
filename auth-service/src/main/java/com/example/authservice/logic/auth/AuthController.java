package com.example.authservice.logic.auth;

import com.example.authservice.jwt.JwtService;
import com.example.authservice.logic.auth.dto.request.LoginRequest;
import com.example.authservice.logic.auth.dto.request.RegisterRequest;
import com.example.authservice.logic.auth.dto.response.TokenResponse;
import com.example.authservice.logic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserService userService;

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        // Перевірка користувача
        if (!userService.validateUser(request.getUsername(), request.getPassword())) {
            log.warn("Invalid login attempt for user {}", request.getUsername());
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtService.generateToken(request.getUsername());
        return new TokenResponse(token);
    }

    @PostMapping("/register")
    public TokenResponse register(@RequestBody RegisterRequest request) {
        userService.registerUser(request.getUsername(), request.getPassword());
        String token = jwtService.generateToken(request.getUsername());
        return new TokenResponse(token);
    }
}
