package com.lifeshots.lifeshotsapi.services;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.request.ProfilePictureRequestDTO;

public interface ProfilePictureService {
    DefaultDTO uploadProfilePicture(ProfilePictureRequestDTO profilePictureRequestDTO);
    DefaultDTO findProfilePictureToUser(String userId);
    DefaultDTO deleteProfilePicture();
}
