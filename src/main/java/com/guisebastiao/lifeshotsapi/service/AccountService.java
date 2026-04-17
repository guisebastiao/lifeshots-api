package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.DeleteAccountRequest;
import com.guisebastiao.lifeshotsapi.dto.request.LanguageRequest;
import com.guisebastiao.lifeshotsapi.dto.request.ProfilePrivacyRequest;
import com.guisebastiao.lifeshotsapi.dto.request.UpdatePasswordRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AccountService {
    DefaultResponse<Void> language(LanguageRequest dto);
    DefaultResponse<Void> profilePrivacy(ProfilePrivacyRequest dto);
    DefaultResponse<Void> updatePassword(UpdatePasswordRequest dto);
    DefaultResponse<Void> deleteAccount(HttpServletRequest request, HttpServletResponse response, DeleteAccountRequest dto);
}
