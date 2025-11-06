package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LoginRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RegisterRequest;
import com.guisebastiao.lifeshotsapi.dto.response.RegisterResponse;
import com.guisebastiao.lifeshotsapi.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    DefaultResponse<UserResponse> login(LoginRequest dto, HttpServletResponse response);
    DefaultResponse<RegisterResponse> register(RegisterRequest dto);
    DefaultResponse<Void> logout(HttpServletResponse response);
    DefaultResponse<Void> refresh(HttpServletRequest request, HttpServletResponse response);
}
