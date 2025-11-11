package com.guisebastiao.lifeshotsapi.mapper.resolver;

import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.ReplyComment;
import com.guisebastiao.lifeshotsapi.repository.LikeReplyCommentRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReplyCommentResolver {

    @Autowired
    private LikeReplyCommentRepository likeReplyCommentRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Named("resolveIsOwner")
    public boolean resolveIsOwner(ReplyComment replyComment) {
        if (replyComment == null || replyComment.getProfile() == null) return false;

        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();
        return replyComment.getProfile().getId().equals(profile.getId());
    }

    @Named("resolveIsLiked")
    public boolean resolveIsLiked(ReplyComment replyComment) {
        if (replyComment == null) return false;

        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();
        return this.likeReplyCommentRepository.existsByReplyCommentAndProfile(replyComment, profile);
    }
}
