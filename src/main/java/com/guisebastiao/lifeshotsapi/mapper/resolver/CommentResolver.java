package com.guisebastiao.lifeshotsapi.mapper.resolver;

import com.guisebastiao.lifeshotsapi.entity.Comment;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.repository.LikeCommentRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentResolver {

    @Autowired
    private LikeCommentRepository likeCommentRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Named("resolveIsOwner")
    public boolean resolveIsOwner(Comment comment) {
        if (comment == null || comment.getProfile() == null) return false;

        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();
        return comment.getProfile().getId().equals(profile.getId());
    }

    @Named("resolveIsLiked")
    public boolean resolveIsLiked(Comment comment) {
        if (comment == null) return false;

        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();
        return this.likeCommentRepository.existsByCommentAndProfile(comment, profile);
    }
}
