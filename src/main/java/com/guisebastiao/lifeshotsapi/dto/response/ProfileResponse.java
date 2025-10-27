package com.guisebastiao.lifeshotsapi.dto.response;

public record ProfileResponse(
        String handle,
        String fullName,
        String bio,
        boolean isPrivate,
        int postsCount,
        int followersCount,
        int followingCount,
        boolean isOwnProfile
) { }
