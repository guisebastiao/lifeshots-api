package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.StoryRequest;
import com.guisebastiao.lifeshotsapi.dto.response.StoryResponse;
import com.guisebastiao.lifeshotsapi.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stories")
public class StoryController {

    @Autowired
    private StoryService storyService;

    @PostMapping
    public ResponseEntity<DefaultResponse<StoryResponse>> createStory(@ModelAttribute StoryRequest dto) {
        DefaultResponse<StoryResponse> response = this.storyService.createStory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{storyId}")
    public ResponseEntity<DefaultResponse<StoryResponse>> findStoryById(@PathVariable String storyId) {
        DefaultResponse<StoryResponse> response = this.storyService.findStoryById(storyId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{storyId}")
    public ResponseEntity<DefaultResponse<StoryResponse>> updateStory(@PathVariable String storyId, @ModelAttribute StoryRequest dto) {
        DefaultResponse<StoryResponse> response = this.storyService.updateStory(storyId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{storyId}")
    public ResponseEntity<DefaultResponse<Void>> deleteStory(@PathVariable String storyId) {
        DefaultResponse<Void> response = this.storyService.deleteStory(storyId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
