package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.FeedStoryControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.response.StoryFeedResponse;
import com.guisebastiao.lifeshotsapi.service.FeedStoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed-stories")
public class FeedStoryController implements FeedStoryControllerDocs {

    private final FeedStoryService feedStoryService;

    public FeedStoryController(FeedStoryService feedStoryService) {
        this.feedStoryService = feedStoryService;
    }

    @Override
    @GetMapping
    public ResponseEntity<DefaultResponse<List<StoryFeedResponse>>> feed(@Valid PaginationParam pagination) {
        DefaultResponse<List<StoryFeedResponse>> response = this.feedStoryService.feed(pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
