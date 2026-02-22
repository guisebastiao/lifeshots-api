package com.guisebastiao.lifeshotsapi.mapper.resolver;

import com.guisebastiao.lifeshotsapi.entity.Post;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.repository.LikePostRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class PostResolver {

    private final LikePostRepository likePostRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public PostResolver(LikePostRepository likePostRepository, AuthenticatedUserProvider authenticatedUserProvider) {
        this.likePostRepository = likePostRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Named("resolveIsOwner")
    public boolean resolveIsOwner(Post post) {
        if (post == null || post.getProfile() == null) return false;

        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();
        return post.getProfile().getId().equals(profile.getId());
    }

    @Named("resolveIsLiked")
    public boolean resolveIsLiked(Post post) {
        if (post == null) return false;

        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();
        return likePostRepository.existsByPostAndProfile(post, profile);
    }
}
