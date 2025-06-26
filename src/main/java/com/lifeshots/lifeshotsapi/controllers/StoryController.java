package com.lifeshots.lifeshotsapi.controllers;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.request.StoryCreateRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.request.StoryUpdateRequestDTO;
import com.lifeshots.lifeshotsapi.services.StoryService;
import jakarta.validation.Valid;
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
    public ResponseEntity<DefaultDTO> createStory(@ModelAttribute @Valid StoryCreateRequestDTO storyCreateRequestDTO) {
        DefaultDTO response = this.storyService.createStory(storyCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{storyId}")
    public ResponseEntity<DefaultDTO> findStoryById(@PathVariable String storyId) {
        DefaultDTO response = this.storyService.findStoryById(storyId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<DefaultDTO> findAllStoriesBelongsFollowers(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "20") int limit) {
        DefaultDTO response = this.storyService.findAllStoriesBelongsFollowers(offset, limit);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{storyId}")
    public ResponseEntity<DefaultDTO> updateStory(@PathVariable String storyId, @RequestBody @Valid StoryUpdateRequestDTO storyUpdateRequestDTO) {
        DefaultDTO response = this.storyService.updateStory(storyId, storyUpdateRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{storyId}")
    public ResponseEntity<DefaultDTO> deleteStory(@PathVariable String storyId) {
        DefaultDTO response = this.storyService.deleteStory(storyId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
