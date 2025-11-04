package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.DeleteAccountRequest;
import com.guisebastiao.lifeshotsapi.dto.request.ProfilePrivacyRequest;
import com.guisebastiao.lifeshotsapi.dto.request.UpdatePasswordRequest;
import com.guisebastiao.lifeshotsapi.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PatchMapping("/privacy")
    public ResponseEntity<DefaultResponse<Void>> setProfilePrivacy(@Valid @RequestBody ProfilePrivacyRequest dto) {
        DefaultResponse<Void> response = accountService.setProfilePrivacy(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/password")
    public ResponseEntity<DefaultResponse<Void>> updatePassword(@Valid @RequestBody UpdatePasswordRequest dto) {
        DefaultResponse<Void> response = this.accountService.updatePassword(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping
    public ResponseEntity<DefaultResponse<Void>> deleteAccount(@RequestBody @Valid DeleteAccountRequest dto) {
        DefaultResponse<Void> response = this.accountService.deleteAccount(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
