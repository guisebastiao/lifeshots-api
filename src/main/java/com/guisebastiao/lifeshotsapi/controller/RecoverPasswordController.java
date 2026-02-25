package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.RecoverPasswordControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.RecoverPasswordTokenParam;
import com.guisebastiao.lifeshotsapi.dto.request.ForgotPasswordRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RecoverPasswordRequest;
import com.guisebastiao.lifeshotsapi.service.RecoverPasswordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recover-password")
public class RecoverPasswordController implements RecoverPasswordControllerDocs {

    private final RecoverPasswordService recoverPasswordService;

    public RecoverPasswordController(RecoverPasswordService recoverPasswordService) {
        this.recoverPasswordService = recoverPasswordService;
    }

    @Override
    @PostMapping("/forgot")
    public ResponseEntity<DefaultResponse<Void>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest dto) {
        DefaultResponse<Void> response = recoverPasswordService.forgotPassword(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @PutMapping
    public ResponseEntity<DefaultResponse<Void>> recoverPassword(@Valid RecoverPasswordTokenParam param, @RequestBody @Valid RecoverPasswordRequest dto) {
        DefaultResponse<Void> response = recoverPasswordService.recoverPassword(param, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
