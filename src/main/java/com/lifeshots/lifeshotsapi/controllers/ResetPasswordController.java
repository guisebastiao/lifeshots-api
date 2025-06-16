package com.lifeshots.lifeshotsapi.controllers;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.request.RecoverPasswordRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.request.ResetPasswordRequestDTO;
import com.lifeshots.lifeshotsapi.services.ResetPasswordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reset-password")
public class ResetPasswordController {

    @Autowired
    private ResetPasswordService resetPasswordService;

    @PostMapping
    public ResponseEntity<DefaultDTO> recoverPassword(@RequestBody @Valid RecoverPasswordRequestDTO recoverPasswordRequestDTO) {
        DefaultDTO response = this.resetPasswordService.recoverPassword(recoverPasswordRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{token}")
    public ResponseEntity<DefaultDTO> resetPassword(@PathVariable String token, @RequestBody @Valid ResetPasswordRequestDTO resetPasswordRequestDTO) {
        DefaultDTO response = this.resetPasswordService.resetPassword(token, resetPasswordRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
