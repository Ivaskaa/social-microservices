package com.example.authservice.jwt;

import com.example.authservice.config.KeyConfig;
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

    public String generateToken(String userId) {
        try {
            JWSSigner signer = new RSASSASigner(keyConfig.getRsaJWK());

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(userId)
                    .expirationTime(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
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
            log.error("Failed to generate JWT", e);
            throw new RuntimeException(e);
        }
    }
}
