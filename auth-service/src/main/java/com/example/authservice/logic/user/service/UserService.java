package com.example.authservice.logic.user.service;

import com.example.authservice.logic.auth.model.request.RegisterRequest;
import com.example.authservice.logic.user.entity.User;
import com.example.authservice.logic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registration(RegisterRequest request) {
        User user = new User();
        user.setLogin(request.getLogin());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        log.info("User {} registered successfully", request.getLogin());
    }
}
