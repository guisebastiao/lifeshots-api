package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LoginRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RefreshRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RegisterRequest;
import com.guisebastiao.lifeshotsapi.dto.response.LoginResponse;
import com.guisebastiao.lifeshotsapi.dto.response.RefreshResponse;
import com.guisebastiao.lifeshotsapi.dto.response.RegisterResponse;

public interface AuthService {
    DefaultResponse<LoginResponse> login(LoginRequest dto);
    DefaultResponse<RegisterResponse> register(RegisterRequest dto);
    DefaultResponse<RefreshResponse> refresh(RefreshRequest dto);
}
