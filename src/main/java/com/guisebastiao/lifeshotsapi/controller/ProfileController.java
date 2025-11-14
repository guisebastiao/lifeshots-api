package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.request.ProfileRequest;
import com.guisebastiao.lifeshotsapi.dto.request.SearchProfileRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;
import com.guisebastiao.lifeshotsapi.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<DefaultResponse<ProfileResponse>> me() {
        DefaultResponse<ProfileResponse> response = this.profileService.me();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<DefaultResponse<PageResponse<ProfileResponse>>> searchProfile(@Valid SearchProfileRequest dto, @Valid PaginationFilter pagination) {
        DefaultResponse<PageResponse<ProfileResponse>> response = this.profileService.searchProfile(dto, pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<DefaultResponse<ProfileResponse>> findProfileById(@PathVariable String profileId) {
        DefaultResponse<ProfileResponse> response = this.profileService.findProfileById(profileId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping
    public ResponseEntity<DefaultResponse<ProfileResponse>> updateProfile(@RequestBody @Valid ProfileRequest dto) {
        DefaultResponse<ProfileResponse> response = this.profileService.updateProfile(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
