package com.lifeshots.lifeshotsapi.controllers;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.request.ProfilePictureRequestDTO;
import com.lifeshots.lifeshotsapi.services.ProfilePictureService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile-picture")
public class ProfilePictureController {

    @Autowired
    private ProfilePictureService profilePictureService;

    @PostMapping
    public ResponseEntity<DefaultDTO> uploadProfilePicture(@ModelAttribute @Valid ProfilePictureRequestDTO profilePictureRequestDTO) {
        DefaultDTO response = profilePictureService.uploadProfilePicture(profilePictureRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<DefaultDTO> findProfilePictureToUser(@PathVariable String userId) {
        DefaultDTO response = profilePictureService.findProfilePictureToUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping
    public ResponseEntity<DefaultDTO> deleteProfilePicture() {
        DefaultDTO response = profilePictureService.deleteProfilePicture();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
