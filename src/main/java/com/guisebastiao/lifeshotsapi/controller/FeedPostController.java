package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.response.PostResponse;
import com.guisebastiao.lifeshotsapi.service.FeedPostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feed-posts")
public class FeedPostController {

    @Autowired
    private FeedPostService feedPostService;

    @GetMapping
    public ResponseEntity<DefaultResponse<PageResponse<PostResponse>>> feed(@Valid PaginationFilter pagination) {
        DefaultResponse<PageResponse<PostResponse>> response = this.feedPostService.feed(pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
