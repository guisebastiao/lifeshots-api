package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.request.CommentRequest;
import com.guisebastiao.lifeshotsapi.dto.response.CommentResponse;
import com.guisebastiao.lifeshotsapi.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<DefaultResponse<CommentResponse>> createComment(@PathVariable String postId, @RequestBody @Valid CommentRequest dto) {
        DefaultResponse<CommentResponse> response = this.commentService.createComment(postId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<DefaultResponse<PageResponse<CommentResponse>>> findAllComments(@PathVariable String postId, @Valid PaginationFilter pagination) {
        DefaultResponse<PageResponse<CommentResponse>> response = this.commentService.findAllComments(postId, pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<DefaultResponse<CommentResponse>> updateComment(@PathVariable String commentId, @RequestBody @Valid CommentRequest dto) {
        DefaultResponse<CommentResponse> response = this.commentService.updateComment(commentId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<DefaultResponse<Void>> deleteComment(@PathVariable String commentId) {
        DefaultResponse<Void> response = this.commentService.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/remove/{postId}/{commentId}")
    public ResponseEntity<DefaultResponse<Void>> removeCommentInPost(@PathVariable String postId, @PathVariable String commentId) {
        DefaultResponse<Void> response = this.commentService.removeCommentInPost(postId, commentId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
