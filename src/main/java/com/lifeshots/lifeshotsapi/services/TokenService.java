package com.lifeshots.lifeshotsapi.services;

import com.lifeshots.lifeshotsapi.dtos.response.TokenResponseDTO;
import com.lifeshots.lifeshotsapi.models.User;

public interface TokenService {
    TokenResponseDTO generateToken(User user);
    String validateToken(String token);
}
