package com.guisebastiao.lifeshotsapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LoginRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    DefaultResponse<Void> login(HttpServletRequest request, HttpServletResponse response, LoginRequest dto) throws JsonProcessingException;
    DefaultResponse<Void> register(RegisterRequest dto);
    DefaultResponse<Void> refresh(HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException;
    DefaultResponse<Void> logout(HttpServletRequest request, HttpServletResponse response);
}
