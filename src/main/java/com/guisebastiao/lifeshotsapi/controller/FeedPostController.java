package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.FeedPostControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.response.PostResponse;
import com.guisebastiao.lifeshotsapi.service.FeedPostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed-posts")
public class FeedPostController implements FeedPostControllerDocs {

    private final FeedPostService feedPostService;

    public FeedPostController(FeedPostService feedPostService) {
        this.feedPostService = feedPostService;
    }

    @GetMapping
    public ResponseEntity<DefaultResponse<List<PostResponse>>> feed(@Valid PaginationParam pagination) {
        DefaultResponse<List<PostResponse>> response = this.feedPostService.feed(pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
