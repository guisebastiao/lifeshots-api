package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.FollowParam;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.FollowRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;

import java.util.List;

public interface FollowService {
    DefaultResponse<Void> follow(String profileId, FollowRequest dto);
    DefaultResponse<List<ProfileResponse>> findAllFollowers(String profileId, FollowParam param, PaginationParam pagination);
}
