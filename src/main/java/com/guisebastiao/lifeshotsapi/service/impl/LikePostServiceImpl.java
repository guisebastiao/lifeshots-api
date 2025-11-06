package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.Paging;
import com.guisebastiao.lifeshotsapi.dto.request.LikePostRequest;
import com.guisebastiao.lifeshotsapi.dto.response.LikePostResponse;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.mapper.LikePostMapper;
import com.guisebastiao.lifeshotsapi.repository.LikePostRepository;
import com.guisebastiao.lifeshotsapi.repository.PostRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.LikePostService;
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
public class LikePostServiceImpl implements LikePostService {

    @Autowired
    private LikePostRepository likePostRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private LikePostMapper likePostMapper;

    @Override
    @Transactional
    public DefaultResponse<Void> likePost(String postId, LikePostRequest dto) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = this.postRepository.findByIdAndNotDeleted(UUIDConverter.toUUID(postId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Story não encontrado"));

        boolean alreadyLiked = this.likePostRepository.alreadyLikedPost(post, profile);

        if (alreadyLiked == dto.like()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, dto.like() ? "Você já curtiu essa publicação" : "Você ainda não curtiu essa publicação");
        }

        if (dto.like()) {
            likePost(post, profile);
            post.setLikeCount(post.getLikeCount() + 1);
        } else {
            unlikePost(post, profile);
            post.setLikeCount(post.getLikeCount() - 1);
        }

        postRepository.save(post);

        String message = dto.like() ? "Publicação curtida com sucesso" : "Publicação descurtida com sucesso";
        return new DefaultResponse<Void>(true, message, null);
    }

    @Override
    public DefaultResponse<PageResponse<LikePostResponse>> findAllLikePost(String postId, PaginationFilter pagination) {
        Post post = this.postRepository.findByIdAndNotDeleted(UUIDConverter.toUUID(postId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Story não encontrado"));

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit(), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<LikePost> resultPage = this.likePostRepository.findAllByPost(post, pageable);

        Paging paging = new Paging(resultPage.getTotalElements(), resultPage.getTotalPages(), pagination.offset(), pagination.limit());

        List<LikePostResponse> dataResponse = resultPage.getContent().stream()
                .map(this.likePostMapper::toDTO)
                .toList();

        PageResponse<LikePostResponse> data = new PageResponse<LikePostResponse>(dataResponse, paging);

        return new DefaultResponse<PageResponse<LikePostResponse>>(true, "Curtidas retornadas com sucesso", data);
    }

    private void likePost(Post post, Profile profile) {
        LikePostId id = new LikePostId(profile.getId(), post.getId());
        LikePost like = new LikePost(id, profile, post);
        likePostRepository.save(like);

        String body = String.format("Sua publicação foi curtida por %s", profile.getUser().getHandle());

        if (!profile.getId().equals(post.getProfile().getId())) {
            this.pushNotificationService.sendNotification(profile, post.getProfile(), "Sua publicação foi Curtida", body, NotificationType.LIKE_IN_POST);;
        }
    }

    private void unlikePost(Post post, Profile profile) {
        LikePostId id = new LikePostId(profile.getId(), post.getId());
        likePostRepository.findById(id).ifPresent(likePostRepository::delete);
    }
}
