package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LoginRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RegisterRequest;
import com.guisebastiao.lifeshotsapi.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    DefaultResponse<AuthResponse> login(HttpServletRequest request, HttpServletResponse response, LoginRequest dto);
    DefaultResponse<AuthResponse> register(RegisterRequest dto);
    DefaultResponse<AuthResponse> refresh(HttpServletRequest request, HttpServletResponse response);
    DefaultResponse<Void> logout(HttpServletRequest request, HttpServletResponse response);
}
