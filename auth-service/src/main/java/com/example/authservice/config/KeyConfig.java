package com.example.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.PrivateKey;
import java.security.PublicKey;

@Configuration
public class KeyConfig {

    @Bean
    public PrivateKey privateKey() throws Exception {
        try (var is = getClass().getResourceAsStream("/keys/private.pem")) {
            return KeyLoader.loadPrivateKey(is);
        }
    }

    @Bean
    public PublicKey publicKey() throws Exception {
        try (var is = getClass().getResourceAsStream("/keys/public.pem")) {
            return KeyLoader.loadPublicKey(is);
        }
    }

}
