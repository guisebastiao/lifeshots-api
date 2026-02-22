package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;

import java.util.List;

public interface RecommendationService {
    DefaultResponse<List<ProfileResponse>> findFriendRecommendations(PaginationParam pagination);
}
