package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.response.FollowResponse;
import com.guisebastiao.lifeshotsapi.enums.FollowType;
import com.guisebastiao.lifeshotsapi.service.FollowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;

@RestController
@RequestMapping("/follows")
public class FollowController {

    @Autowired
    private FollowService followService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(FollowType.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(FollowType.valueOf(text.toUpperCase()));
            }
        });
    }

    @PostMapping("/{profileId}")
    public ResponseEntity<DefaultResponse<Void>> createFollow(@PathVariable String profileId) {
        DefaultResponse<Void> response = this.followService.follow(profileId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<DefaultResponse<PageResponse<FollowResponse>>> findAllMyFollows(@RequestParam FollowType type, @Valid PaginationFilter pagination) {
        DefaultResponse<PageResponse<FollowResponse>> response = this.followService.findAllMyFollowers(type, pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<DefaultResponse<PageResponse<FollowResponse>>> findAllFollows(@PathVariable String profileId, @RequestParam FollowType type, @Valid PaginationFilter pagination) {
        DefaultResponse<PageResponse<FollowResponse>> response = this.followService.findAllFollowers(profileId, type, pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<DefaultResponse<Void>> deleteFollow(@PathVariable String profileId) {
        DefaultResponse<Void> response = this.followService.unfollow(profileId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
