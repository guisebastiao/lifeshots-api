package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.request.LikePostRequest;
import com.guisebastiao.lifeshotsapi.dto.response.LikePostResponse;
import com.guisebastiao.lifeshotsapi.service.LikePostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like-posts")
public class LikePostController {

    @Autowired
    private LikePostService likePostService;

    @PostMapping("/{postId}")
    public ResponseEntity<DefaultResponse<Void>> likePost(@PathVariable String postId, @RequestBody @Valid LikePostRequest dto) {
        DefaultResponse<Void> response = this.likePostService.likePost(postId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<DefaultResponse<PageResponse<LikePostResponse>>> findAllLikePost(@PathVariable String postId, @Valid PaginationFilter pagination) {
        DefaultResponse<PageResponse<LikePostResponse>> response = this.likePostService.findAllLikePost(postId, pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
