package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LikeCommentRequest;

public interface LikeCommentService {
    DefaultResponse<Void> likeComment(String commentId, LikeCommentRequest dto);
}
