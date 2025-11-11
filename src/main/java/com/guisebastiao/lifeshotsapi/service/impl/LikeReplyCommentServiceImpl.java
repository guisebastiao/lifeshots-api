package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LikeReplyCommentRequest;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.repository.LikeReplyCommentRepository;
import com.guisebastiao.lifeshotsapi.repository.ReplyCommentRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.LikeReplyCommentService;
import com.guisebastiao.lifeshotsapi.service.PushNotificationService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LikeReplyCommentServiceImpl implements LikeReplyCommentService {

    @Autowired
    private LikeReplyCommentRepository likeReplyCommentRepository;

    @Autowired
    private ReplyCommentRepository replyCommentRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Override
    public DefaultResponse<Void> likeReplyComment(String replyCommentId, LikeReplyCommentRequest dto) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();

        ReplyComment replyComment = this.replyCommentRepository.findByIdAndNotDeletedAndNotRemoved(UUIDConverter.toUUID(replyCommentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrado"));

        boolean alreadyLiked = this.likeReplyCommentRepository.existsByReplyCommentAndProfile(replyComment, profile);

        if (alreadyLiked == dto.like()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, dto.like() ? "Você já curtiu esse comentário" : "Você ainda não curtiu esse comentário");
        }

        if (dto.like()) {
            likeComment(replyComment, profile);
            replyComment.setLikeCount(replyComment.getLikeCount() + 1);
        } else {
            unlikeComment(replyComment, profile);
            replyComment.setLikeCount(replyComment.getLikeCount() - 1);
        }

        replyCommentRepository.save(replyComment);

        String message = dto.like() ? "Comentário curtido com sucesso" : "Comentário descurtido com sucesso";
        return new DefaultResponse<Void>(true, message, null);
    }

    private void likeComment(ReplyComment replyComment, Profile profile) {
        LikeReplyCommentId id = new LikeReplyCommentId(profile.getId(), replyComment.getId());
        LikeReplyComment like = new LikeReplyComment(id, profile, replyComment);
        likeReplyCommentRepository.save(like);

        String body = String.format("Seu comentário foi curtido por %s", profile.getUser().getHandle());

        if (!profile.getId().equals(replyComment.getProfile().getId())) {
            this.pushNotificationService.sendNotification(profile, replyComment.getProfile(), "Seu comentário foi curtido", body, NotificationType.LIKE_IN_COMMENT_REPLY);;
        }
    }

    private void unlikeComment(ReplyComment replyComment, Profile profile) {
        LikeReplyCommentId id = new LikeReplyCommentId(profile.getId(), replyComment.getId());
        likeReplyCommentRepository.findById(id).ifPresent(likeReplyCommentRepository::delete);
    }
}
