package com.example.userservice.feign.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthFeignClientFallback implements AuthFeignClient {
    @Override
    public String getJwks() {
        log.warn("Fallback triggered for getJwks");
        return null;
    }
}
