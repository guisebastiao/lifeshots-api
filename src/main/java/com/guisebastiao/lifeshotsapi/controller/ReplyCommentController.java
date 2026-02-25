package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.ReplyCommentControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.ReplyCommentRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ReplyCommentResponse;
import com.guisebastiao.lifeshotsapi.service.ReplyCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reply-comments")
public class ReplyCommentController implements ReplyCommentControllerDocs {

    private final ReplyCommentService replyCommentService;

    public ReplyCommentController(ReplyCommentService replyCommentService) {
        this.replyCommentService = replyCommentService;
    }

    @Override
    @PostMapping("/{commentId}")
    public ResponseEntity<DefaultResponse<ReplyCommentResponse>> createReplyComment(@PathVariable String commentId, @RequestBody @Valid ReplyCommentRequest dto) {
        DefaultResponse<ReplyCommentResponse> response = this.replyCommentService.createReplyComment(commentId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/{commentId}")
    public ResponseEntity<DefaultResponse<List<ReplyCommentResponse>>> findAllReplyComments(@PathVariable String commentId, @Valid PaginationParam pagination) {
        DefaultResponse<List<ReplyCommentResponse>> response = this.replyCommentService.findAllReplyComments(commentId, pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @PatchMapping("/{replyCommentId}")
    public ResponseEntity<DefaultResponse<ReplyCommentResponse>> updateReplyComment(@PathVariable String replyCommentId, @RequestBody @Valid ReplyCommentRequest dto) {
        DefaultResponse<ReplyCommentResponse> response = this.replyCommentService.updateReplyComment(replyCommentId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @DeleteMapping("/{replyCommentId}")
    public ResponseEntity<DefaultResponse<Void>> deleteReplyComment(@PathVariable String replyCommentId) {
        DefaultResponse<Void> response = this.replyCommentService.deleteReplyComment(replyCommentId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @DeleteMapping("/remove/{postId}/{replyCommentId}")
    public ResponseEntity<DefaultResponse<Void>> removeReplyCommentInComment(@PathVariable String postId, @PathVariable String replyCommentId) {
        DefaultResponse<Void> response = this.replyCommentService.removeReplyCommentInComment(postId, replyCommentId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
