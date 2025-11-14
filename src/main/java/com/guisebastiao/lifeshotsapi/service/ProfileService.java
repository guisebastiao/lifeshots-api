package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.request.ProfileRequest;
import com.guisebastiao.lifeshotsapi.dto.request.SearchProfileRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;

public interface ProfileService {
    DefaultResponse<ProfileResponse> me();
    DefaultResponse<PageResponse<ProfileResponse>> searchProfile(SearchProfileRequest search, PaginationFilter pagination);
    DefaultResponse<ProfileResponse> findProfileById(String profileId);
    DefaultResponse<ProfileResponse> updateProfile(ProfileRequest dto);
}
