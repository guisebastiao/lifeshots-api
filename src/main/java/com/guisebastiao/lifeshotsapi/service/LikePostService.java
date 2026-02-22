package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.LikePostRequest;
import com.guisebastiao.lifeshotsapi.dto.response.LikePostResponse;

import java.util.List;

public interface LikePostService {
    DefaultResponse<Void> likePost(String postId, LikePostRequest dto);
    DefaultResponse<List<LikePostResponse>> findAllLikePost(String postId, PaginationParam pagination);
}
