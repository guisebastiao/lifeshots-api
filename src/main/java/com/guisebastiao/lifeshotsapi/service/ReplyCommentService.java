package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.ReplyCommentRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ReplyCommentResponse;

import java.util.List;

public interface ReplyCommentService {
    DefaultResponse<ReplyCommentResponse> createReplyComment(String commentId, ReplyCommentRequest dto);
    DefaultResponse<List<ReplyCommentResponse>> findAllReplyComments(String commentId, PaginationParam pagination);
    DefaultResponse<ReplyCommentResponse> updateReplyComment(String replyCommentId, ReplyCommentRequest dto);
    DefaultResponse<Void> deleteReplyComment(String replyCommentId);
    DefaultResponse<Void> removeReplyCommentInComment(String postId, String replyCommentId);
}
