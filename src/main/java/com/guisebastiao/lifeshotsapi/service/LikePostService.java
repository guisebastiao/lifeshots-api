package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.LikePostRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;

import java.util.List;

public interface LikePostService {
    DefaultResponse<Void> likePost(String postId, LikePostRequest dto);
    DefaultResponse<List<ProfileResponse>> findAllLikePost(String postId, PaginationParam pagination);
}
