package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.LikeStoryControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.LikeStoryRequest;
import com.guisebastiao.lifeshotsapi.dto.response.LikeStoryResponse;
import com.guisebastiao.lifeshotsapi.service.LikeStoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/like-stories")
public class LikeStoryController implements LikeStoryControllerDocs {

    private final LikeStoryService likeStoryService;

    public LikeStoryController(LikeStoryService likeStoryService) {
        this.likeStoryService = likeStoryService;
    }

    @Override
    @PostMapping("/{storyId}")
    public ResponseEntity<DefaultResponse<Void>> likeStory(@PathVariable String storyId, @RequestBody LikeStoryRequest dto) {
        DefaultResponse<Void> response = likeStoryService.likeStory(storyId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @GetMapping("/{storyId}")
    public ResponseEntity<DefaultResponse<List<LikeStoryResponse>>> findAllLikeStory(@PathVariable String storyId, @Valid PaginationParam pagination) {
        DefaultResponse<List<LikeStoryResponse>> response = likeStoryService.findAllLikeStory(storyId, pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
