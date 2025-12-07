package com.example.userservice.feign.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "auth-service",
        url = "${service-auth.url}",
        fallback = AuthFeignClientFallback.class
)
public interface AuthFeignClient {

    @GetMapping("/jwt/get-jwks")
    String getJwks();
}
