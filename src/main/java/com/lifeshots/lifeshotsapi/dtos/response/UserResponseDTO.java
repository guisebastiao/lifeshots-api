package com.lifeshots.lifeshotsapi.dtos.response;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String nickname,
        String name,
        String surname,
        String bio,
        String email,
        Integer amountFollowing,
        Integer amountFollowers,
        Integer amountPosts,
        Boolean privateAccount,
        ProfilePictureResponseDTO profilePicture
){ }
