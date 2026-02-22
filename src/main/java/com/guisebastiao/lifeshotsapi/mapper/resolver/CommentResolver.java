package com.guisebastiao.lifeshotsapi.mapper.resolver;

import com.guisebastiao.lifeshotsapi.entity.Comment;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.repository.LikeCommentRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class CommentResolver {

    private final LikeCommentRepository likeCommentRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public CommentResolver(LikeCommentRepository likeCommentRepository, AuthenticatedUserProvider authenticatedUserProvider) {
        this.likeCommentRepository = likeCommentRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Named("resolveIsOwner")
    public boolean resolveIsOwner(Comment comment) {
        if (comment == null || comment.getProfile() == null) return false;

        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();
        return comment.getProfile().getId().equals(profile.getId());
    }

    @Named("resolveIsLiked")
    public boolean resolveIsLiked(Comment comment) {
        if (comment == null) return false;

        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();
        return likeCommentRepository.existsByCommentAndProfile(comment, profile);
    }
}
