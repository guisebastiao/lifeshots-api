package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.AuthControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LoginRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RegisterRequest;
import com.guisebastiao.lifeshotsapi.dto.response.AuthResponse;
import com.guisebastiao.lifeshotsapi.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<DefaultResponse<AuthResponse>> login(HttpServletResponse httpResponse, @RequestBody @Valid LoginRequest dto) {
        DefaultResponse<AuthResponse> response = authService.login(httpResponse, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @GetMapping("/login/google")
    public void googleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<DefaultResponse<AuthResponse>> register(@RequestBody @Valid RegisterRequest dto) {
        DefaultResponse<AuthResponse> response = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<DefaultResponse<Void>> refresh(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        DefaultResponse<Void> response = authService.refresh(httpRequest, httpResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<DefaultResponse<Void>> logout(HttpServletResponse httpResponse) {
        DefaultResponse<Void> response = authService.logout(httpResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
