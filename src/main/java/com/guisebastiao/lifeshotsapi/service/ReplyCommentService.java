package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.request.ReplyCommentRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ReplyCommentResponse;

public interface ReplyCommentService {
    DefaultResponse<ReplyCommentResponse> createReplyComment(String commentId, ReplyCommentRequest dto);
    DefaultResponse<PageResponse<ReplyCommentResponse>> findAllReplyComments(String commentId, PaginationFilter pagination);
    DefaultResponse<ReplyCommentResponse> updateReplyComment(String replyCommentId, ReplyCommentRequest dto);
    DefaultResponse<Void> deleteReplyComment(String replyCommentId);
    DefaultResponse<Void> removeReplyCommentInComment(String postId, String replyCommentId);
}
