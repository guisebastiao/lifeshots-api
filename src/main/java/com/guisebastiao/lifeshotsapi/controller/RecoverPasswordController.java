package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.ForgotPasswordRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RecoverPasswordRequest;
import com.guisebastiao.lifeshotsapi.service.RecoverPasswordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recover-password")
public class RecoverPasswordController {

    @Autowired
    private RecoverPasswordService recoverPasswordService;

    @PostMapping("/forgot")
    public ResponseEntity<DefaultResponse<Void>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest dto) {
        DefaultResponse<Void> response = recoverPasswordService.forgotPassword(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{token}")
    public ResponseEntity<DefaultResponse<Void>> recoverPassword(@PathVariable String token, @RequestBody @Valid RecoverPasswordRequest dto) {
        DefaultResponse<Void> response = recoverPasswordService.recoverPassword(token, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
