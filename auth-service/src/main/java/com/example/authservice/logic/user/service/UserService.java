package com.example.authservice.logic.user.service;

import com.example.authservice.logic.auth.dto.request.LoginRequest;
import com.example.authservice.logic.auth.dto.request.RegisterRequest;
import com.example.authservice.logic.user.entity.User;
import com.example.authservice.logic.user.repository.UserRepository;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .permissions(Set.of("USER_READ", "USER_WRITE"))
                .build();

        return userRepository.save(user);
    }

    public User validateUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid credentials");
        }

        return user;
    }
}
