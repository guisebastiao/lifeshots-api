package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.CommentRequest;
import com.guisebastiao.lifeshotsapi.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    DefaultResponse<CommentResponse> createComment(String postId, CommentRequest dto);
    DefaultResponse<List<CommentResponse>> findAllComments(String postId, PaginationParam pagination);
    DefaultResponse<CommentResponse> updateComment(String commentId, CommentRequest dto);
    DefaultResponse<Void> deleteComment(String commentId);
    DefaultResponse<Void> removeCommentInPost(String postId, String commentId);
}
