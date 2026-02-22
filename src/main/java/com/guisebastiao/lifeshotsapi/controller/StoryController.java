package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.StoryControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.StoryRequest;
import com.guisebastiao.lifeshotsapi.dto.request.StoryUpdateRequest;
import com.guisebastiao.lifeshotsapi.dto.response.StoryResponse;
import com.guisebastiao.lifeshotsapi.service.StoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stories")
public class StoryController implements StoryControllerDocs {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DefaultResponse<StoryResponse>> createStory(@ModelAttribute @Valid StoryRequest dto) {
        DefaultResponse<StoryResponse> response = this.storyService.createStory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{storyId}")
    public ResponseEntity<DefaultResponse<StoryResponse>> findStoryById(@PathVariable String storyId) {
        DefaultResponse<StoryResponse> response = this.storyService.findStoryById(storyId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<DefaultResponse<List<StoryResponse>>> findStoriesByAuthUser() {
        DefaultResponse<List<StoryResponse>> response = this.storyService.findStoriesByAuthUser();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{storyId}")
    public ResponseEntity<DefaultResponse<StoryResponse>> updateStory(@PathVariable String storyId, @RequestBody @Valid StoryUpdateRequest dto) {
        DefaultResponse<StoryResponse> response = this.storyService.updateStory(storyId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{storyId}")
    public ResponseEntity<DefaultResponse<Void>> deleteStory(@PathVariable String storyId) {
        DefaultResponse<Void> response = this.storyService.deleteStory(storyId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
