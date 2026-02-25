package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.ProfileControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.ProfileRequest;
import com.guisebastiao.lifeshotsapi.dto.request.SearchProfileRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;
import com.guisebastiao.lifeshotsapi.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profiles")
public class ProfileController implements ProfileControllerDocs {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<DefaultResponse<ProfileResponse>> me() {
        DefaultResponse<ProfileResponse> response = this.profileService.me();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<DefaultResponse<List<ProfileResponse>>> searchProfile(@Valid SearchProfileRequest dto, @Valid PaginationParam pagination) {
        DefaultResponse<List<ProfileResponse>> response = this.profileService.searchProfile(dto, pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @GetMapping("/{profileId}")
    public ResponseEntity<DefaultResponse<ProfileResponse>> findProfileById(@PathVariable String profileId) {
        DefaultResponse<ProfileResponse> response = this.profileService.findProfileById(profileId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @PatchMapping
    public ResponseEntity<DefaultResponse<ProfileResponse>> updateProfile(@RequestBody @Valid ProfileRequest dto) {
        DefaultResponse<ProfileResponse> response = this.profileService.updateProfile(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
