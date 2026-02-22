package com.guisebastiao.lifeshotsapi.mapper.resolver;

import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.Story;
import com.guisebastiao.lifeshotsapi.repository.LikeStoryRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class StoryResolver {

    private final LikeStoryRepository likeStoryRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public StoryResolver(LikeStoryRepository likeStoryRepository, AuthenticatedUserProvider authenticatedUserProvider) {
        this.likeStoryRepository = likeStoryRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Named("resolveIsOwner")
    public boolean resolveIsOwner(Story story) {
        if (story == null || story.getProfile() == null) return false;

        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();
        return story.getProfile().getId().equals(profile.getId());
    }

    @Named("resolveIsLiked")
    public boolean resolveIsLiked(Story story) {
        if (story == null) return false;

        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();
        return likeStoryRepository.existsByStoryAndProfile(story, profile);
    }
}
