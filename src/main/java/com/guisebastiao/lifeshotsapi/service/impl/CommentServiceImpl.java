package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.Paging;
import com.guisebastiao.lifeshotsapi.dto.request.CommentRequest;
import com.guisebastiao.lifeshotsapi.dto.response.CommentResponse;
import com.guisebastiao.lifeshotsapi.entity.Comment;
import com.guisebastiao.lifeshotsapi.entity.Post;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.mapper.CommentMapper;
import com.guisebastiao.lifeshotsapi.repository.CommentRepository;
import com.guisebastiao.lifeshotsapi.repository.PostRepository;
import com.guisebastiao.lifeshotsapi.repository.ProfileRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.CommentService;
import com.guisebastiao.lifeshotsapi.service.PushNotificationService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import jakarta.transaction.Transactional;
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
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private CommentMapper commentMapper;

    @Override
    @Transactional
    public DefaultResponse<CommentResponse> createComment(String postId, CommentRequest dto) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = this.postRepository.findByIdAndNotDeleted(UUIDConverter.toUUID(postId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicação não encontrada"));

        Comment comment = this.commentMapper.toEntity(dto);
        comment.setProfile(profile);
        comment.setPost(post);

        Comment savedComment = this.commentRepository.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);
        this.postRepository.save(post);

        String body = String.format("Você recebeu um novo comentário de %s", profile.getUser().getHandle());
        this.pushNotificationService.sendNotification(profile, post.getProfile(), "Alguém comentou em sua publicação", body, NotificationType.COMMENT_ON_POST);

        CommentResponse data = this.commentMapper.toDTO(savedComment);

        return new DefaultResponse<CommentResponse>(true, "Comentário criado com sucesso", data);
    }

    @Override
    public DefaultResponse<PageResponse<CommentResponse>> findAllComments(String postId, PaginationFilter pagination) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = this.postRepository.findByIdAndNotDeleted(UUIDConverter.toUUID(postId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicação não encontrada"));

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit(), Sort.by(Sort.Order.desc("likeCount"), Sort.Order.desc("createdAt")));

        Page<Comment> resultPage = this.commentRepository.findAllByPost(post, pageable);

        Paging paging = new Paging(resultPage.getTotalElements(), resultPage.getTotalPages(), pagination.offset(), pagination.limit());

        List<CommentResponse> dataResponse = resultPage.getContent().stream()
                .map(this.commentMapper::toDTO)
                .toList();

        PageResponse<CommentResponse> data = new PageResponse<CommentResponse>(dataResponse, paging);

        return new DefaultResponse<PageResponse<CommentResponse>>(true, "Comentários retornandos com sucesso", data);
    }

    @Override
    @Transactional
    public DefaultResponse<CommentResponse> updateComment(String commentId, CommentRequest dto) {
        Comment comment = this.findCommentAndBelongsToTheProfile(commentId);

        this.commentMapper.updateComment(dto, comment);

        Comment savedComment = this.commentRepository.save(comment);

        CommentResponse data = this.commentMapper.toDTO(savedComment);

        return new DefaultResponse<CommentResponse>(true, "Comentário editado com sucesso", data);
    }

    @Override
    @Transactional
    public DefaultResponse<Void> deleteComment(String commentId) {
        Comment comment = this.findCommentAndBelongsToTheProfile(commentId);
        comment.setDeleted(true);

        this.commentRepository.save(comment);

        Post post = comment.getPost();
        post.setCommentCount(post.getCommentCount() - 1);

        this.postRepository.save(post);

        return new DefaultResponse<Void>(true, "Comentário excluido com sucesso", null);
    }

    @Override
    @Transactional
    public DefaultResponse<Void> removeCommentInPost(String postId, String commentId) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = this.postRepository.findByIdAndNotDeleted(UUIDConverter.toUUID(postId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicação não encontrada"));

        Comment comment = this.commentRepository.findByIdAndNotDeletedAndNotRemoved(UUIDConverter.toUUID(commentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrado"));

        if (!post.getProfile().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não permissão para remover esse comentário");
        }

        if (comment.getProfile().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você pode apenas fazer a exclusão do próprio comentário");
        }

        comment.setRemoved(true);
        this.commentRepository.save(comment);

        return new DefaultResponse<Void>(true, "Comentário removido com sucesso", null);
    }

    private Comment findCommentAndBelongsToTheProfile(String commentId) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Comment comment = this.commentRepository.findByIdAndNotDeletedAndNotRemoved(UUIDConverter.toUUID(commentId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrada"));

        if (!profile.getId().equals(comment.getProfile().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para manipular esse comentário");
        }

        return comment;
    }
}
