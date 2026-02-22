package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.FollowControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.FollowParam;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.response.FollowResponse;
import com.guisebastiao.lifeshotsapi.service.FollowService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follows")
public class FollowController implements FollowControllerDocs {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/{profileId}")
    public ResponseEntity<DefaultResponse<Void>> follow(@PathVariable String profileId) {
        DefaultResponse<Void> response = this.followService.follow(profileId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<DefaultResponse<List<FollowResponse>>> findAllMyFollows(@Valid FollowParam param, @Valid PaginationParam pagination) {
        DefaultResponse<List<FollowResponse>> response = this.followService.findAllMyFollowers(param, pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<DefaultResponse<List<FollowResponse>>> findAllFollows(@PathVariable String profileId, @Valid FollowParam param, @Valid PaginationParam pagination) {
        DefaultResponse<List<FollowResponse>> response = this.followService.findAllFollowers(profileId, param, pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<DefaultResponse<Void>> unfollow(@PathVariable String profileId) {
        DefaultResponse<Void> response = this.followService.unfollow(profileId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
