package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.request.CommentRequest;
import com.guisebastiao.lifeshotsapi.dto.response.CommentResponse;

public interface CommentService {
    DefaultResponse<CommentResponse> createComment(String postId, CommentRequest dto);
    DefaultResponse<PageResponse<CommentResponse>> findAllComments(String postId, PaginationFilter pagination);
    DefaultResponse<CommentResponse> updateComment(String commentId, CommentRequest dto);
    DefaultResponse<Void> deleteComment(String commentId);
    DefaultResponse<Void> removeCommentInPost(String postId, String commentId);
}
