package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.ProfilePictureControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.ProfilePictureRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfilePictureResponse;
import com.guisebastiao.lifeshotsapi.service.ProfilePictureService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile-picture")
public class ProfilePictureController implements ProfilePictureControllerDocs {

    private final ProfilePictureService profilePictureService;

    public ProfilePictureController(ProfilePictureService profilePictureService) {
        this.profilePictureService = profilePictureService;
    }

    @PostMapping
    public ResponseEntity<DefaultResponse<ProfilePictureResponse>> uploadProfilePicture(@ModelAttribute @Valid ProfilePictureRequest dto) {
        DefaultResponse<ProfilePictureResponse> response = profilePictureService.uploadProfilePicture(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<DefaultResponse<ProfilePictureResponse>> findProfilePictureById(@PathVariable String profileId) {
        DefaultResponse<ProfilePictureResponse> response = profilePictureService.findProfilePictureById(profileId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping
    public ResponseEntity<DefaultResponse<Void>> deleteProfilePicture() {
        DefaultResponse<Void> response = this.profilePictureService.deleteProfilePicture();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
