package com.example.authservice.logic.auth.service;

import com.example.authservice.config.jwt.JwtService;
import com.example.authservice.logic.auth.entity.RefreshToken;
import com.example.authservice.logic.auth.entity.User;
import com.example.authservice.logic.auth.model.request.RegisterRequest;
import com.example.authservice.logic.auth.model.response.TokenResponse;
import com.example.authservice.logic.auth.repository.RefreshTokenRepository;
import com.example.authservice.logic.auth.repository.UserRepository;
import com.example.authservice.utils.exceptions.AuthException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    private static final long REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000; // 7 days

    @Transactional
    public TokenResponse login(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException(
                        User.class.getSimpleName() + "with " + login + " not found"));

        String accessToken = jwtService.generateAccessToken(user.getLogin());
        String refreshToken = createRefreshToken(user);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public TokenResponse registration(RegisterRequest request) {
        User user = User.builder()
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .permissions(Set.of())
                .build();

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user.getLogin());
        String refreshToken = createRefreshToken(user);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public TokenResponse refresh(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new AuthException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthException("Refresh token expired or revoked");
        }

        String accessToken = jwtService.generateAccessToken(refreshToken.getUser().getLogin());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .build();
    }

    private String createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION_MS))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

}
