package com.guisebastiao.lifeshotsapi.mapper.resolver;

import com.guisebastiao.lifeshotsapi.entity.Post;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.Story;
import com.guisebastiao.lifeshotsapi.repository.LikePostRepository;
import com.guisebastiao.lifeshotsapi.repository.LikeStoryRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostResolver {

    @Autowired
    private LikePostRepository likePostRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Named("resolveIsOwner")
    public boolean resolveIsOwner(Post post) {
        if (post == null || post.getProfile() == null) return false;

        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();
        return post.getProfile().getId().equals(profile.getId());
    }

    @Named("resolveIsLiked")
    public boolean resolveIsLiked(Post post) {
        if (post == null) return false;

        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();
        return this.likePostRepository.existsByPostAndProfile(post, profile);
    }
}
