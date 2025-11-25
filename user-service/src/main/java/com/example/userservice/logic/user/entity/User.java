package com.example.userservice.logic.user.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    // На кого користувач підписаний
    @ManyToMany
    @JoinTable(
            name = "user_subscriptions",
            joinColumns = @JoinColumn(name = "subscriber_id"),
            inverseJoinColumns = @JoinColumn(name = "subscribed_to_id")
    )
    private Set<User> subscriptions = new HashSet<>();

    // Хто підписаний на користувача
    @ManyToMany(mappedBy = "subscriptions")
    private Set<User> subscribers = new HashSet<>();
}
