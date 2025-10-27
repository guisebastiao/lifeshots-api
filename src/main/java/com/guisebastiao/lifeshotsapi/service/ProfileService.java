package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.ProfileRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;

public interface ProfileService {
    DefaultResponse<ProfileResponse> me();
    DefaultResponse<ProfileResponse> findProfileById(String profileId);
    DefaultResponse<ProfileResponse> updateProfile(ProfileRequest dto);
}
