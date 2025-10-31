package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.response.FollowResponse;
import com.guisebastiao.lifeshotsapi.enums.FollowType;

public interface FollowService {
    DefaultResponse<Void> follow(String profileId);
    DefaultResponse<PageResponse<FollowResponse>> findAllMyFollowers(FollowType type, PaginationFilter pagination);
    DefaultResponse<PageResponse<FollowResponse>> findAllFollowers(String profileId, FollowType type, PaginationFilter pagination);
    DefaultResponse<Void> unfollow(String profileId);
}
