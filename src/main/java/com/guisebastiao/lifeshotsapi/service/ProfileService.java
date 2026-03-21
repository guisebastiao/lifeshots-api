package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.ProfileRequest;
import com.guisebastiao.lifeshotsapi.dto.request.SearchProfileRequest;
import com.guisebastiao.lifeshotsapi.dto.response.PostResponse;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;

import java.util.List;

public interface ProfileService {
    DefaultResponse<ProfileResponse> me();
    DefaultResponse<List<ProfileResponse>> searchProfile(SearchProfileRequest search, PaginationParam pagination);
    DefaultResponse<ProfileResponse> findProfileByHandle(String handle);
    DefaultResponse<ProfileResponse> findProfileById(String profileId);
    DefaultResponse<List<PostResponse>> findPosts(String profileId, PaginationParam pagination);
    DefaultResponse<ProfileResponse> updateProfile(ProfileRequest dto);
}
