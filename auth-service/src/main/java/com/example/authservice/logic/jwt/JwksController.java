package com.example.authservice.logic.jwt;

import com.example.authservice.config.jwt.KeyConfig;
import com.nimbusds.jose.jwk.JWKSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class JwksController {

    private final KeyConfig keyConfig;

    @GetMapping("/jwt/get-jwks")
    public String getJwks() {
        JWKSet jwkSet = keyConfig.getJwkSet();
        return jwkSet.toJSONObject().toString();
    }
}
