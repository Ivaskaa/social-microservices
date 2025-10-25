package com.example.userservice.logic.user.dto.response;

public record UserResponse(
        Long id,
        String name,
        String email
) { }
