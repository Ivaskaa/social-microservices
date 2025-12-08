package com.example.authservice.logic.auth.validator;

import com.example.authservice.logic.auth.model.request.LoginRequest;
import com.example.authservice.logic.auth.model.request.RegisterRequest;
import com.example.authservice.logic.auth.repository.UserRepository;
import com.example.authservice.utils.errors_validation.ErrorUtils;
import com.example.authservice.utils.exceptions.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthValidator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void validateLogin(LoginRequest request) {
        userRepository.findByLogin(request.getLogin())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .orElseThrow(() -> new AuthException("Username or password is incorrect"));

    }

    public void validateRegistration(RegisterRequest request, BindingResult bindingResult) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            bindingResult.addError(ErrorUtils.createFieldError("confirmPassword", "Passwords do not match"));
        }

        userRepository.findByLogin(request.getLogin())
                .ifPresent(user -> bindingResult.addError(
                        ErrorUtils.createFieldError("username", "User with this username already exists")));
    }
}
