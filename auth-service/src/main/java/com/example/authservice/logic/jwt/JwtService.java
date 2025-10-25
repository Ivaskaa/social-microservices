package com.example.authservice.logic.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final PrivateKey privateKey;

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("permissions", user.getPermissions())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
}
