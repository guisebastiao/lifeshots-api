package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.response.StoryFeedResponse;
import com.guisebastiao.lifeshotsapi.service.FeedStoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feed-stories")
public class FeedStoryController {

    @Autowired
    private FeedStoryService feedStoryService;

    @GetMapping
    public ResponseEntity<DefaultResponse<PageResponse<StoryFeedResponse>>> feed(@Valid PaginationFilter pagination) {
        DefaultResponse<PageResponse<StoryFeedResponse>> response = this.feedStoryService.feed(pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
