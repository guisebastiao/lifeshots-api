package com.lifeshots.lifeshotsapi.services;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.request.RecoverPasswordRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.request.ResetPasswordRequestDTO;

public interface ResetPasswordService {
    DefaultDTO recoverPassword(RecoverPasswordRequestDTO recoverPasswordRequestDTO);
    DefaultDTO resetPassword(String token, ResetPasswordRequestDTO resetPasswordRequestDTO);
}
