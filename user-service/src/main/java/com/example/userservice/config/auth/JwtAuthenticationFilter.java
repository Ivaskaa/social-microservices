package com.example.userservice.config.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing Authorization header");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            JWK jwk = jwtConfig.getJwkSet().getKeyByKeyId(signedJWT.getHeader().getKeyID());
            if (jwk == null) {
                log.warn("JWK not found for keyId {}", signedJWT.getHeader().getKeyID());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Key not found");
                return;
            }

            RSAKey rsaKey = (RSAKey) jwk;
            JWSVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());

            if (!signedJWT.verify(verifier)) {
                log.warn("JWT verification failed for token with keyId {}", signedJWT.getHeader().getKeyID());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
                return;
            }

            String userId = signedJWT.getJWTClaimsSet().getSubject();
            request.setAttribute("userId", userId);

        } catch (JOSEException e) {
            log.error("JWT verification error", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT verification error");
            return;
        } catch (Exception e) {
            log.error("JWT validation failed", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT validation failed");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
