package com.example.authservice.logic.auth.service;

import com.example.authservice.config.jwt.KeyConfig;
import com.example.authservice.utils.exceptions.FieldErrorException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final long DEFAULT_REFRESH_TTL_MS = 7L * 24 * 60 * 60 * 1000; // 7 days

    private final Map<String, RefreshRecord> store = new ConcurrentHashMap<>(); // jti -> record
    private final KeyConfig keyConfig;

    public String createAndStore(String subject) {
        try {
            String jti = UUID.randomUUID().toString();
            Date exp = new Date(System.currentTimeMillis() + DEFAULT_REFRESH_TTL_MS);

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .jwtID(jti)
                    .expirationTime(exp)
                    .claim("token_type", "refresh")
                    .build();

            SignedJWT jwt = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256)
                            .keyID(keyConfig.getRsaJWK().getKeyID())
                            .build(),
                    claims
            );

            jwt.sign(new RSASSASigner(keyConfig.getRsaJWK()));

            store.put(jti, new RefreshRecord(subject, exp));
            return jwt.serialize();
        } catch (JOSEException e) {
            log.error("Failed to create refresh token", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies and consumes a refresh token (single-use). Returns subject on success.
     */
    public String consumeRefreshToken(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);

            // Verify signature against the key specified by token's kid
            String kid = jwt.getHeader().getKeyID();
            RSAKey rsaKey = kid != null ? keyConfig.getKeyById(kid) : null;
            if (rsaKey == null) {
                throw invalid("Unknown key id");
            }
            RSAPublicKey publicKey = rsaKey.toRSAPublicKey();
            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            if (!jwt.verify(verifier)) {
                throw invalid("Invalid refresh token signature");
            }

            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            if (!"refresh".equals(claims.getStringClaim("token_type"))) {
                throw invalid("Wrong token type");
            }

            Date exp = claims.getExpirationTime();
            if (exp == null || exp.before(new Date())) {
                throw invalid("Refresh token expired");
            }

            String jti = claims.getJWTID();
            if (jti == null) {
                throw invalid("Missing token id");
            }

            RefreshRecord record = store.remove(jti); // single-use
            if (record == null) {
                throw invalid("Refresh token already used or revoked");
            }

            // Optional: double-check expiry from store
            if (record.expiresAt.before(new Date())) {
                throw invalid("Refresh token expired");
            }

            return record.subject;

        } catch (FieldErrorException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Failed to consume refresh token", e);
            throw invalid("Invalid refresh token");
        }
    }

    private FieldErrorException invalid(String message) {
        return new FieldErrorException("refreshToken", message);
    }

    private record RefreshRecord(String subject, Date expiresAt) {}
}
