package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LoginRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RefreshRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RegisterRequest;
import com.guisebastiao.lifeshotsapi.dto.response.LoginResponse;
import com.guisebastiao.lifeshotsapi.dto.response.RefreshResponse;
import com.guisebastiao.lifeshotsapi.dto.response.RegisterResponse;
import com.guisebastiao.lifeshotsapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<DefaultResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest dto) {
        DefaultResponse<LoginResponse> response = this.authService.login(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<DefaultResponse<RegisterResponse>> register(@RequestBody @Valid RegisterRequest dto) {
        DefaultResponse<RegisterResponse> response = this.authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<DefaultResponse<RefreshResponse>> refresh(@RequestBody @Valid RefreshRequest dto) {
        DefaultResponse<RefreshResponse> response = this.authService.refresh(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
