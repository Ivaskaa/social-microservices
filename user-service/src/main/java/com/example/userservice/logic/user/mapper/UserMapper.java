package com.example.userservice.logic.user.mapper;

import com.example.userservice.logic.user.dto.response.UserResponse;
import com.example.userservice.logic.user.entity.User;

public interface UserMapper {
    static UserResponse mapUserToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getUsername()
        );
    }
}
