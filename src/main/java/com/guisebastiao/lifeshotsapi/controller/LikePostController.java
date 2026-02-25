package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.LikePostControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.LikePostRequest;
import com.guisebastiao.lifeshotsapi.dto.response.LikePostResponse;
import com.guisebastiao.lifeshotsapi.service.LikePostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/like-posts")
public class LikePostController implements LikePostControllerDocs {

    private final LikePostService likePostService;

    public LikePostController(LikePostService likePostService) {
        this.likePostService = likePostService;
    }

    @Override
    @PostMapping("/{postId}")
    public ResponseEntity<DefaultResponse<Void>> likePost(@PathVariable String postId, @RequestBody @Valid LikePostRequest dto) {
        DefaultResponse<Void> response = this.likePostService.likePost(postId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @GetMapping("/{postId}")
    public ResponseEntity<DefaultResponse<List<LikePostResponse>>> findAllLikePost(@PathVariable String postId, @Valid PaginationParam pagination) {
        DefaultResponse<List<LikePostResponse>> response = this.likePostService.findAllLikePost(postId, pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
