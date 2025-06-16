package com.lifeshots.lifeshotsapi.controllers;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.request.UpdateAccountRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.request.UpdatePasswordRequestDTO;
import com.lifeshots.lifeshotsapi.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<DefaultDTO> findById(@PathVariable String userId) {
        DefaultDTO response = userService.findById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/update-password")
    public ResponseEntity<DefaultDTO> updatePassword(@RequestBody @Valid UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        DefaultDTO response = userService.updatePassword(updatePasswordRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping
    public ResponseEntity<DefaultDTO> updateAccount(@RequestBody @Valid UpdateAccountRequestDTO updateAccountRequestDTO) {
        DefaultDTO response = userService.updateAccount(updateAccountRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping
    public ResponseEntity<DefaultDTO> deleteAccount() {
        DefaultDTO response = userService.deleteAccount();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
