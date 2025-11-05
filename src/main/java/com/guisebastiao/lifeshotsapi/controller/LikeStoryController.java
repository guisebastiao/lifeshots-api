package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.request.LikeStoryRequest;
import com.guisebastiao.lifeshotsapi.dto.response.LikeStoryResponse;
import com.guisebastiao.lifeshotsapi.service.LikeStoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like-stories")
public class LikeStoryController {

    @Autowired
    private LikeStoryService likeStoryService;

    @PostMapping("/{storyId}")
    public ResponseEntity<DefaultResponse<Void>> likeStory(@PathVariable String storyId, @RequestBody LikeStoryRequest dto) {
        DefaultResponse<Void> response = likeStoryService.likeStory(storyId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{storyId}")
    public ResponseEntity<DefaultResponse<PageResponse<LikeStoryResponse>>> findAllLikeStory(@PathVariable String storyId, @Valid PaginationFilter pagination) {
        DefaultResponse<PageResponse<LikeStoryResponse>> response = likeStoryService.findAllLikeStory(storyId, pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
