package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LikeReplyCommentRequest;

public interface LikeReplyCommentService {
    DefaultResponse<Void> likeReplyComment(String replyCommentId, LikeReplyCommentRequest dto);
}
