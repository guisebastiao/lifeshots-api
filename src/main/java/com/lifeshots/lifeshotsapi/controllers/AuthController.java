package com.lifeshots.lifeshotsapi.controllers;

import com.lifeshots.lifeshotsapi.utils.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private TokenGenerator tokenGenerator;

    @PostMapping("/login")
    public ResponseEntity<?> login() {
        return ResponseEntity.ok(tokenGenerator.generateToken());
    }

}
