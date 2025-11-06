package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.request.LikePostRequest;
import com.guisebastiao.lifeshotsapi.dto.response.LikePostResponse;

public interface LikePostService {
    DefaultResponse<Void> likePost(String postId, LikePostRequest dto);
    DefaultResponse<PageResponse<LikePostResponse>> findAllLikePost(String postId, PaginationFilter pagination);
}
