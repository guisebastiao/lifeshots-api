package com.lifeshots.lifeshotsapi.dtos.response;

public record UserResponseDTO(
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
