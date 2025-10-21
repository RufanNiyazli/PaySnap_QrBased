package com.project.paysnap.controller;

import com.project.paysnap.dto.AuthResponse;
import com.project.paysnap.dto.LoginRequest;
import com.project.paysnap.dto.RegisterRequest;

public interface IAuthController {
    public void register(RegisterRequest registerRequest);

    public AuthResponse login(LoginRequest loginRequest);
}
