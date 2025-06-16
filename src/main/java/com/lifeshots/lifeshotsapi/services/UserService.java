package com.lifeshots.lifeshotsapi.services;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.request.UpdateAccountRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.request.UpdatePasswordRequestDTO;

public interface UserService {
    DefaultDTO findById(String id);
    DefaultDTO updatePassword(UpdatePasswordRequestDTO updatePasswordRequestDTO);
    DefaultDTO updateAccount(UpdateAccountRequestDTO updateAccountDTO);
    DefaultDTO deleteAccount();
}
