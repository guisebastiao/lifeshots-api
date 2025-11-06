package com.guisebastiao.lifeshotsapi.mapper.resolver;

import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.Story;
import com.guisebastiao.lifeshotsapi.repository.LikeStoryRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StoryResolver {

    @Autowired
    private LikeStoryRepository likeStoryRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Named("resolveIsOwner")
    public boolean resolveIsOwner(Story story) {
        if (story == null || story.getProfile() == null) return false;

        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();
        return story.getProfile().getId().equals(profile.getId());
    }

    @Named("resolveIsLiked")
    public boolean resolveIsLiked(Story story) {
        if (story == null) return false;

        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();
        return this.likeStoryRepository.existsByStoryAndProfile(story, profile);
    }
}
