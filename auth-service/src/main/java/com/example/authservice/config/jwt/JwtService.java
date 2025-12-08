package com.example.authservice.config.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final KeyConfig keyConfig;

    private static final long TOKEN_EXPIRATION_MS = 15 * 60 * 1000; // 15 min

    public String generateAccessToken(String subject) {
        try {
            JWSSigner signer = new RSASSASigner(keyConfig.getRsaJWK());

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .expirationTime(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MS))
                    .claim("token_type", "access")
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new com.nimbusds.jose.JWSHeader.Builder(JWSAlgorithm.RS256)
                            .keyID(keyConfig.getRsaJWK().getKeyID())
                            .build(),
                    claims
            );

            signedJWT.sign(signer);
            return signedJWT.serialize();

        } catch (JOSEException e) {
            log.error("Failed to generate access JWT", e);
            throw new RuntimeException(e);
        }
    }
}
