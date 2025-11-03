package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.ProfilePictureRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfilePictureResponse;

public interface ProfilePictureService {
    DefaultResponse<ProfilePictureResponse> uploadProfilePicture(ProfilePictureRequest dto);
    DefaultResponse<ProfilePictureResponse> findProfilePictureById(String profileId);
    DefaultResponse<Void> deleteProfilePicture();
}
