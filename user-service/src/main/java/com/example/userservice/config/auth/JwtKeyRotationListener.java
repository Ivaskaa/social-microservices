package com.example.userservice.config.auth;

import com.example.userservice.feign.auth.AuthFeignClient;
import com.nimbusds.jose.jwk.JWKSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtKeyRotationListener {

    private final JwtConfig jwtConfig;
    private final AuthFeignClient authFeignClient;

    @KafkaListener(topics = "auth.key-rotation", groupId = "user-service-group")
    public void onKeyRotation(String message) {
        try {
            String jwksJson = authFeignClient.getJwks();
            jwtConfig.setJwkSet(JWKSet.parse(jwksJson));
            log.info("JWKS updated successfully after key rotation: {}", message);
        } catch (Exception e) {
            log.error("Failed to refresh JWKS from AuthService", e);
        }
    }

}
