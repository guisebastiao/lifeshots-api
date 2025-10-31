package com.guisebastiao.lifeshotsapi.dto.response;

import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String handle,
        String fullName,
        String bio,
        boolean isPrivate,
        int postsCount,
        int followersCount,
        int followingCount,
        boolean isOwnProfile
) { }
