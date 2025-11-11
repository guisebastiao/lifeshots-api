package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.Paging;
import com.guisebastiao.lifeshotsapi.dto.request.ReplyCommentRequest;
import com.guisebastiao.lifeshotsapi.dto.response.CommentResponse;
import com.guisebastiao.lifeshotsapi.dto.response.ReplyCommentResponse;
import com.guisebastiao.lifeshotsapi.entity.Comment;
import com.guisebastiao.lifeshotsapi.entity.Post;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.ReplyComment;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.mapper.ReplyCommentMapper;
import com.guisebastiao.lifeshotsapi.repository.CommentRepository;
import com.guisebastiao.lifeshotsapi.repository.PostRepository;
import com.guisebastiao.lifeshotsapi.repository.ProfileRepository;
import com.guisebastiao.lifeshotsapi.repository.ReplyCommentRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.PushNotificationService;
import com.guisebastiao.lifeshotsapi.service.ReplyCommentService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReplyCommentServiceImpl implements ReplyCommentService {

    @Autowired
    private ReplyCommentRepository replyCommentRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private ReplyCommentMapper replyCommentMapper;

    @Override
    public DefaultResponse<ReplyCommentResponse> createReplyComment(String commentId, ReplyCommentRequest dto) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Comment comment = this.commentRepository.findByIdAndNotDeletedAndNotRemoved(UUIDConverter.toUUID(commentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrado"));

        ReplyComment replyComment = this.replyCommentMapper.toEntity(dto);
        replyComment.setProfile(profile);
        replyComment.setComment(comment);

        ReplyComment savedReplyComment = this.replyCommentRepository.save(replyComment);

        comment.setReplyCommentCount(comment.getReplyCommentCount() + 1);
        this.commentRepository.save(comment);

        String body = String.format("Você recebeu uma nova resposta em seu comentário de %s", profile.getUser().getHandle());
        this.pushNotificationService.sendNotification(profile, comment.getProfile(), "Alguém respodeu seu comentário", body, NotificationType.COMMENT_ON_POST);

        ReplyCommentResponse data = this.replyCommentMapper.toDTO(savedReplyComment);

        return new DefaultResponse<ReplyCommentResponse>(true, "Comentário criado com sucesso", data);
    }

    @Override
    public DefaultResponse<PageResponse<ReplyCommentResponse>> findAllReplyComments(String commentId, PaginationFilter pagination) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Comment comment = this.commentRepository.findByIdAndNotDeletedAndNotRemoved(UUIDConverter.toUUID(commentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrado"));

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit(), Sort.by(Sort.Order.desc("likeCount"), Sort.Order.desc("createdAt")));

        Page<ReplyComment> resultPage = this.replyCommentRepository.findAllByComment(comment, pageable);

        Paging paging = new Paging(resultPage.getTotalElements(), resultPage.getTotalPages(), pagination.offset(), pagination.limit());

        List<ReplyCommentResponse> dataResponse = resultPage.getContent().stream()
                .map(this.replyCommentMapper::toDTO)
                .toList();

        PageResponse<ReplyCommentResponse> data = new PageResponse<ReplyCommentResponse>(dataResponse, paging);

        return new DefaultResponse<PageResponse<ReplyCommentResponse>>(true, "Comentários retornandas com sucesso", data);
    }

    @Override
    public DefaultResponse<ReplyCommentResponse> updateReplyComment(String replyCommentId, ReplyCommentRequest dto) {
        ReplyComment replyComment = this.findReplyCommentAndBelongsToTheProfile(replyCommentId);

        this.replyCommentMapper.updateReplyComment(dto, replyComment);

        ReplyComment savedComment = this.replyCommentRepository.save(replyComment);

        ReplyCommentResponse data = this.replyCommentMapper.toDTO(savedComment);

        return new DefaultResponse<ReplyCommentResponse>(true, "Comentário editado com sucesso", data);
    }

    @Override
    public DefaultResponse<Void> deleteReplyComment(String replyCommentId) {
        ReplyComment replyComment = this.findReplyCommentAndBelongsToTheProfile(replyCommentId);
        replyComment.setDeleted(true);

        this.replyCommentRepository.save(replyComment);

        Comment comment = replyComment.getComment();
        comment.setReplyCommentCount(comment.getReplyCommentCount() - 1);

        this.commentRepository.save(comment);

        return new DefaultResponse<Void>(true, "Comentário excluido com sucesso", null);
    }

    @Override
    public DefaultResponse<Void> removeReplyCommentInComment(String postId, String replyCommentId) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = this.postRepository.findByIdAndNotDeleted(UUIDConverter.toUUID(postId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicação não encontrada"));

        ReplyComment replyComment = this.replyCommentRepository.findByIdAndNotDeletedAndNotRemoved(UUIDConverter.toUUID(replyCommentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrado"));

        if (!post.getProfile().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não permissão para remover esse comentário");
        }

        if (replyComment.getProfile().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você pode apenas fazer a exclusão do próprio comentário");
        }

        replyComment.setRemoved(true);
        this.replyCommentRepository.save(replyComment);

        return new DefaultResponse<Void>(true, "Comentário removido com sucesso", null);
    }

    private ReplyComment findReplyCommentAndBelongsToTheProfile(String replyCommentId) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();

        ReplyComment replyComment = this.replyCommentRepository.findByIdAndNotDeletedAndNotRemoved(UUIDConverter.toUUID(replyCommentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resposta de comentário não encontrada"));

        if (!profile.getId().equals(replyComment.getProfile().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para manipular esse comentário");
        }

        return replyComment;
    }
}
