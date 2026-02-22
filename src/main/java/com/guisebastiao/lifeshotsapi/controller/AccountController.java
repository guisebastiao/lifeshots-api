package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.AccountControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.DeleteAccountRequest;
import com.guisebastiao.lifeshotsapi.dto.request.ProfilePrivacyRequest;
import com.guisebastiao.lifeshotsapi.dto.request.UpdatePasswordRequest;
import com.guisebastiao.lifeshotsapi.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController implements AccountControllerDocs {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PatchMapping("/privacy")
    public ResponseEntity<DefaultResponse<Void>> setProfilePrivacy(@Valid @RequestBody ProfilePrivacyRequest dto) {
        DefaultResponse<Void> response = accountService.setProfilePrivacy(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/password")
    public ResponseEntity<DefaultResponse<Void>> updatePassword(@Valid @RequestBody UpdatePasswordRequest dto) {
        DefaultResponse<Void> response = accountService.updatePassword(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping
    public ResponseEntity<DefaultResponse<Void>> deleteAccount(@RequestBody @Valid DeleteAccountRequest dto) {
        DefaultResponse<Void> response = accountService.deleteAccount(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
