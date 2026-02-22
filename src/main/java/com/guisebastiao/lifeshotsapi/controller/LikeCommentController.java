package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.LikeCommentControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LikeCommentRequest;
import com.guisebastiao.lifeshotsapi.service.LikeCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like-comments")
public class LikeCommentController implements LikeCommentControllerDocs {

    private final LikeCommentService likeCommentService;

    public LikeCommentController(LikeCommentService likeCommentService) {
        this.likeCommentService = likeCommentService;
    }

    @PostMapping("/{commentId}")
    public ResponseEntity<DefaultResponse<Void>> likeComment(@PathVariable String commentId, @RequestBody @Valid LikeCommentRequest dto) {
        DefaultResponse<Void> response = this.likeCommentService.likeComment(commentId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
