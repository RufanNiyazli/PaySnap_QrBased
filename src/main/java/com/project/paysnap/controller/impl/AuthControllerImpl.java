package com.project.paysnap.controller.impl;

import com.project.paysnap.controller.IAuthController;
import com.project.paysnap.dto.AuthResponse;
import com.project.paysnap.dto.LoginRequest;
import com.project.paysnap.dto.RegisterRequest;
import com.project.paysnap.service.BlacklistService;
import com.project.paysnap.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements IAuthController {
    private final IAuthService authService;
    private final BlacklistService blacklistService;

    @PostMapping("/public/register")
    @Override
    public void register(
            @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);


    }

    @Override
    @PostMapping("/public/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @Override
    @PostMapping("public/logout")
    public Map<String, String> logout(
            @RequestHeader String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String token = authHeader.substring(7);
        long expirationMillis = 3600000;
        blacklistService.blacklistToken(token, expirationMillis);
        return Map.of("message", "Logged out successfully");
    }

}
