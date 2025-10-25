package com.example.authservice.logic.auth;

import com.example.authservice.logic.auth.dto.request.LoginRequest;
import com.example.authservice.logic.auth.dto.request.RegisterRequest;
import com.example.authservice.logic.auth.dto.response.TokenResponse;
import com.example.authservice.logic.jwt.JwtService;
import com.example.authservice.logic.user.entity.User;
import com.example.authservice.logic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PublicKey publicKey;

    public TokenResponse register(RegisterRequest request) {
        User user = userService.register(request);
        return new TokenResponse(jwtService.generateToken(user));
    }

    public TokenResponse login(LoginRequest request) {
        User user = userService.validateUser(request);
        return new TokenResponse(jwtService.generateToken(user));
    }

    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
}
