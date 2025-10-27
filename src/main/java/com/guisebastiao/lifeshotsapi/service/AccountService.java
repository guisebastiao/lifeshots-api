package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.DeleteAccountRequest;
import com.guisebastiao.lifeshotsapi.dto.request.UpdatePasswordRequest;

public interface AccountService {
    DefaultResponse<Void> updatePassword(UpdatePasswordRequest dto);
    DefaultResponse<Void> deleteAccount(DeleteAccountRequest dto);
}
