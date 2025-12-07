package com.example.userservice.config.auth;

import com.example.userservice.feign.auth.AuthFeignClient;
import com.nimbusds.jose.jwk.JWKSet;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Getter
@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final AtomicReference<JWKSet> jwkSet = new AtomicReference<>();
    private final AuthFeignClient authFeignClient;

    public JWKSet getJwkSet() {
        return jwkSet.get();
    }

    public void setJwkSet(JWKSet jwkSet) {
        this.jwkSet.set(jwkSet);
    }

    @PostConstruct
    private void loadJWKS() {
        try {
            String jwksJson = authFeignClient.getJwks();
            jwkSet.set(JWKSet.parse(jwksJson));
            log.info("JWKS initial load successful");
        } catch (Exception e) {
            log.error("Failed initial JWKS load", e);
        }
    }
}
