package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LikeReplyCommentRequest;
import com.guisebastiao.lifeshotsapi.service.LikeReplyCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like-reply-comments")
public class LikeReplyCommentController {

    private final LikeReplyCommentService likeReplyCommentService;

    public LikeReplyCommentController(LikeReplyCommentService likeReplyCommentService) {
        this.likeReplyCommentService = likeReplyCommentService;
    }

    @PostMapping("/{replyCommentId}")
    public ResponseEntity<DefaultResponse<Void>> likeReplyComment(@PathVariable String replyCommentId, @RequestBody @Valid LikeReplyCommentRequest dto) {
        DefaultResponse<Void> response = this.likeReplyCommentService.likeReplyComment(replyCommentId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
