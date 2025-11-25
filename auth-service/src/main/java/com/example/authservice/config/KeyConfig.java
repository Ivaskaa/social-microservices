package com.example.authservice.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class KeyConfig {

    private final Map<String, RSAKey> activeKeys = new ConcurrentHashMap<>();
    private final AtomicReference<RSAKey> currentKey = new AtomicReference<>();

    private final KafkaTemplate<String, String> kafkaTemplate;

    @PostConstruct
    public void firstGenerateNewKey() {
        generateNewKey();
    }

    public RSAKey getRsaJWK() {
        return currentKey.get();
    }

    public JWKSet getJwkSet() {
        return new JWKSet(new ArrayList<>(activeKeys.values()));
    }

    public synchronized void generateNewKey() {
        try {
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
            keyGenerator.initialize(2048);
            KeyPair keyPair = keyGenerator.generateKeyPair();

            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            RSAKey newKey = new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(UUID.randomUUID().toString())
                    .build();

            currentKey.set(newKey);
            activeKeys.put(newKey.getKeyID(), newKey);

            log.info("RSA key pair generated with keyId {}", newKey.getKeyID());

            kafkaTemplate.send("auth.key-rotation", newKey.getKeyID());
            log.info("Published key rotation event to Kafka: {}", newKey.getKeyID());

        } catch (Exception e) {
            log.error("Failed to generate RSA key", e);
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedRateString = "${auth.key-rotation.interval-ms:43200000}") // 12h default
    public void scheduledKeyRotation() {
        log.info("Scheduled key rotation triggered");
        generateNewKey();
    }
}
