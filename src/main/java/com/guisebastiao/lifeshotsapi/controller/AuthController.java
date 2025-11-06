package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LoginRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RegisterRequest;
import com.guisebastiao.lifeshotsapi.dto.response.RegisterResponse;
import com.guisebastiao.lifeshotsapi.dto.response.UserResponse;
import com.guisebastiao.lifeshotsapi.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<DefaultResponse<UserResponse>> login(@RequestBody @Valid LoginRequest dto, HttpServletResponse httpResponse) {
        DefaultResponse<UserResponse> response = this.authService.login(dto, httpResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<DefaultResponse<RegisterResponse>> register(@RequestBody @Valid RegisterRequest dto) {
        DefaultResponse<RegisterResponse> response = this.authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<DefaultResponse<Void>> logout(HttpServletResponse httpResponse) {
        DefaultResponse<Void> response = this.authService.logout(httpResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<DefaultResponse<Void>> refresh(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        DefaultResponse<Void> response = this.authService.refresh(httpRequest, httpResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
