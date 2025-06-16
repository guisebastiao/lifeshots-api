package com.lifeshots.lifeshotsapi.services;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.request.LoginRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.request.RegisterRequestDTO;

public interface AuthService {
    DefaultDTO login(LoginRequestDTO loginRequestDTO);
    DefaultDTO register(RegisterRequestDTO registerRequestDTO);
}
