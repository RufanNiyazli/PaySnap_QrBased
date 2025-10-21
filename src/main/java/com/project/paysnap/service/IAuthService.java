package com.project.paysnap.service;

import com.project.paysnap.dto.AuthResponse;
import com.project.paysnap.dto.LoginRequest;
import com.project.paysnap.dto.RegisterRequest;

public interface IAuthService {
    public void register(RegisterRequest registerRequest);

    public AuthResponse login(LoginRequest loginRequest);
}
