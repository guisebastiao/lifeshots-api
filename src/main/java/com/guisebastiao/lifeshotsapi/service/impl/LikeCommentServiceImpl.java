package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LikeCommentRequest;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.repository.CommentRepository;
import com.guisebastiao.lifeshotsapi.repository.LikeCommentRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.LikeCommentService;
import com.guisebastiao.lifeshotsapi.service.PushNotificationService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LikeCommentServiceImpl implements LikeCommentService {

    @Autowired
    private LikeCommentRepository likeCommentRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Override
    @Transactional
    public DefaultResponse<Void> likeComment(String commentId, LikeCommentRequest dto) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Comment comment = this.commentRepository.findByIdAndNotDeletedAndNotRemoved(UUIDConverter.toUUID(commentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrado"));

        boolean alreadyLiked = this.likeCommentRepository.existsByCommentAndProfile(comment, profile);

        if (alreadyLiked == dto.like()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, dto.like() ? "Você já curtiu esse comentário" : "Você ainda não curtiu esse comentário");
        }

        if (dto.like()) {
            likeComment(comment, profile);
            comment.setLikeCount(comment.getLikeCount() + 1);
        } else {
            unlikeComment(comment, profile);
            comment.setLikeCount(comment.getLikeCount() - 1);
        }

        commentRepository.save(comment);

        String message = dto.like() ? "Comentário curtido com sucesso" : "Comentário descurtido com sucesso";
        return new DefaultResponse<Void>(true, message, null);
    }

    private void likeComment(Comment comment, Profile profile) {
        LikeCommentId id = new LikeCommentId(profile.getId(), comment.getId());
        LikeComment like = new LikeComment(id, profile, comment);
        likeCommentRepository.save(like);

        String body = String.format("Seu comentário foi curtido por %s", profile.getUser().getHandle());

        if (!profile.getId().equals(comment.getProfile().getId())) {
            this.pushNotificationService.sendNotification(profile, comment.getProfile(), "Seu comentário foi curtido", body, NotificationType.LIKE_IN_POST);;
        }
    }

    private void unlikeComment(Comment comment, Profile profile) {
        LikeCommentId id = new LikeCommentId(profile.getId(), comment.getId());
        likeCommentRepository.findById(id).ifPresent(likeCommentRepository::delete);
    }
}
