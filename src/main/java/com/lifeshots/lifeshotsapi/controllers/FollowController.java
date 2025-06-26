package com.lifeshots.lifeshotsapi.controllers;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.request.FollowRequestDTO;
import com.lifeshots.lifeshotsapi.services.FollowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/follows")
public class FollowController {

    @Autowired
    private FollowService followService;

    @PostMapping
    public ResponseEntity<DefaultDTO> follow(@RequestBody @Valid FollowRequestDTO followRequestDTO) {
        DefaultDTO response = this.followService.follow(followRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping
    public ResponseEntity<DefaultDTO> unfollow(@RequestBody @Valid FollowRequestDTO followRequestDTO) {
        DefaultDTO response = this.followService.unfollow(followRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
