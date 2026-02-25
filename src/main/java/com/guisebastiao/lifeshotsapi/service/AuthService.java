package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LoginRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RegisterRequest;
import com.guisebastiao.lifeshotsapi.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {
    DefaultResponse<AuthResponse> login(HttpServletResponse response, LoginRequest dto);
    DefaultResponse<AuthResponse> register(RegisterRequest dto);
    DefaultResponse<AuthResponse> authenticated(Authentication authentication);
    DefaultResponse<Void> refresh(HttpServletRequest request, HttpServletResponse response);
    DefaultResponse<Void> logout(HttpServletResponse response);
}
