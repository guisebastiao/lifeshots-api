package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.ForgotPasswordRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RecoverPasswordRequest;

public interface RecoverPasswordService {
    DefaultResponse<Void> forgotPassword(ForgotPasswordRequest dto);
    DefaultResponse<Void> recoverPassword(String token, RecoverPasswordRequest dto);
}
