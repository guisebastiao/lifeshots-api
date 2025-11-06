package com.guisebastiao.lifeshotsapi.mapper.resolver;

import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.repository.FollowRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfileResolver {

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private FollowRepository followRepository;

    @Named("resolveGetHandle")
    public String resolveGetHandle(Profile profile) {
        if (profile == null) {
            return null;
        }

        return profile.getUser().getHandle();
    }

    @Named("resolveIsOwnProfile")
    public boolean resolveIsOwnProfile(Profile profile) {
        if (profile == null) return false;

        User authUser = this.authenticatedUserProvider.getAuthenticatedUser();

        return profile.getUser().getId().equals(authUser.getProfile().getId());
    }

    @Named("resolveIsFollowing")
    public boolean resolveIsFollowing(Profile profile) {
        if (profile == null) return false;
        Profile authProfile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();
        return this.followRepository.existsByFollowerAndFollowing(authProfile, profile);
    }

    @Named("resolveIsFollower")
    public boolean resolveIsFollower(Profile profile) {
        if (profile == null) return false;
        Profile authProfile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();
        return this.followRepository.existsByFollowingAndFollower(authProfile, profile);
    }
}
