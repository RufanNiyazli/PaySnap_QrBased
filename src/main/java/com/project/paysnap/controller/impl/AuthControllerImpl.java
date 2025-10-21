package com.project.paysnap.controller.impl;

import com.project.paysnap.controller.IAuthController;
import com.project.paysnap.dto.AuthResponse;
import com.project.paysnap.dto.LoginRequest;
import com.project.paysnap.dto.RegisterRequest;
import com.project.paysnap.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements IAuthController {
    private final IAuthService authService;

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
}
