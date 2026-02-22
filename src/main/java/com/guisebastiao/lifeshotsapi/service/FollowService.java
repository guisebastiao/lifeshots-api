package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.FollowParam;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.response.FollowResponse;
import com.guisebastiao.lifeshotsapi.enums.FollowType;

import java.util.List;

public interface FollowService {
    DefaultResponse<Void> follow(String profileId);
    DefaultResponse<List<FollowResponse>> findAllMyFollowers(FollowParam param, PaginationParam pagination);
    DefaultResponse<List<FollowResponse>> findAllFollowers(String profileId, FollowParam param, PaginationParam pagination);
    DefaultResponse<Void> unfollow(String profileId);
}
