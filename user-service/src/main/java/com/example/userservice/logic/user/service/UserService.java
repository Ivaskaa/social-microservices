package com.example.userservice.logic.user.service;

import com.example.userservice.logic.user.dto.response.UserResponse;
import com.example.userservice.logic.user.entity.User;
import com.example.userservice.logic.user.mapper.UserMapper;
import com.example.userservice.logic.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponse getUserResponseById(Long id) {
        log.info("get user response by id: {}", id);
        User user = getUserById(id);
        UserResponse userResponse = UserMapper.mapUserToUserResponse(user);
        log.info("success get user response by id {}", id);
        return userResponse;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class.getName() + "by id " + id + " not found"));
    }

    // Підписка на іншого користувача
    public void subscribe(Long subscriberId, Long subscribedToId) {
        if(subscriberId.equals(subscribedToId)) {
            throw new IllegalArgumentException("User cannot subscribe to himself");
        }
        User subscriber = getUserById(subscriberId);
        User subscribedTo = getUserById(subscribedToId);

        boolean added = subscriber.getSubscriptions().add(subscribedTo);
        if(added) {
            userRepository.save(subscriber); // JPA автоматично оновить subscribers
            log.info("User {} subscribed to {}", subscriberId, subscribedToId);
        }
    }

    // Відписка
    public void unsubscribe(Long subscriberId, Long subscribedToId) {
        User subscriber = getUserById(subscriberId);
        User subscribedTo = getUserById(subscribedToId);

        boolean removed = subscriber.getSubscriptions().remove(subscribedTo);
        if(removed) {
            userRepository.save(subscriber);
            log.info("User {} unsubscribed from {}", subscriberId, subscribedToId);
        }
    }
}
