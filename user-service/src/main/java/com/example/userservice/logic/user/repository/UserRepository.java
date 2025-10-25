package com.example.userservice.logic.user.repository;

import com.example.userservice.logic.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> { }
