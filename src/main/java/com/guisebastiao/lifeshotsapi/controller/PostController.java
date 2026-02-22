package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.PostControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.PostRequest;
import com.guisebastiao.lifeshotsapi.dto.request.PostUpdateRequest;
import com.guisebastiao.lifeshotsapi.dto.response.PostResponse;
import com.guisebastiao.lifeshotsapi.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostController implements PostControllerDocs {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<DefaultResponse<PostResponse>> createPost(@ModelAttribute @Valid PostRequest dto) {
        DefaultResponse<PostResponse> response = postService.createPost(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<DefaultResponse<PostResponse>> findPostById(@PathVariable String postId) {
        DefaultResponse<PostResponse> response = postService.findPostById(postId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<DefaultResponse<PostResponse>> updatePost(@PathVariable String postId, @ModelAttribute @Valid PostUpdateRequest dto) {
        DefaultResponse<PostResponse> response = postService.updatePost(postId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<DefaultResponse<Void>> deletePost(@PathVariable String postId) {
        DefaultResponse<Void> response = postService.deletePost(postId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
