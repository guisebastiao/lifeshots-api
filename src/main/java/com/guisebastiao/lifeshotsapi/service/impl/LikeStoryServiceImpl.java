package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.Paging;
import com.guisebastiao.lifeshotsapi.dto.request.LikeStoryRequest;
import com.guisebastiao.lifeshotsapi.dto.response.LikeStoryResponse;
import com.guisebastiao.lifeshotsapi.dto.response.NotificationResponse;
import com.guisebastiao.lifeshotsapi.entity.*;
import com.guisebastiao.lifeshotsapi.enums.NotificationType;
import com.guisebastiao.lifeshotsapi.mapper.LikeStoryMapper;
import com.guisebastiao.lifeshotsapi.repository.LikeStoryRepository;
import com.guisebastiao.lifeshotsapi.repository.StoryRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.LikeStoryService;
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
public class LikeStoryServiceImpl implements LikeStoryService {

    @Autowired
    private LikeStoryRepository likeStoryRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private LikeStoryMapper likeStoryMapper;

    @Override
    @Transactional
    public DefaultResponse<Void> likeStory(String storyId, LikeStoryRequest dto) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Story story = this.storyRepository.findByIdAndNotDeleted(UUIDConverter.toUUID(storyId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Story não encontrado"));

        boolean alreadyLiked = this.likeStoryRepository.alreadyLikedStory(story, profile);

        if (story.getProfile().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você não pode curtir seu próprio story");
        }

        if (alreadyLiked == dto.like()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, dto.like() ? "Você já curtiu este story" : "Você ainda não curtiu este story");
        }

        if (dto.like()) {
            likeStory(story, profile);
            story.setLikeCount(story.getLikeCount() + 1);
        } else {
            unlikeStory(story, profile);
            story.setLikeCount(story.getLikeCount() - 1);
        }

        storyRepository.save(story);

        String message = dto.like() ? "Story curtido com sucesso" : "Story descurtido com sucesso";
        return new DefaultResponse<Void>(true, message, null);
    }

    @Override
    public DefaultResponse<PageResponse<LikeStoryResponse>> findAllLikeStory(String storyId, PaginationFilter pagination) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Story story = this.storyRepository.findByIdAndNotDeleted(UUIDConverter.toUUID(storyId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Story não encontrado"));

        if (!story.getProfile().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para visualizar quem curtiu esse story");
        }

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit(), Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<LikeStory> resultPage = this.likeStoryRepository.findAllByStory(story, pageable);

        Paging paging = new Paging(resultPage.getTotalElements(), resultPage.getTotalPages(), pagination.offset(), pagination.limit());

        List<LikeStoryResponse> dataResponse = resultPage.getContent().stream()
                .map(this.likeStoryMapper::toDTO)
                .toList();

        PageResponse<LikeStoryResponse> data = new PageResponse<LikeStoryResponse>(dataResponse, paging);

        return new DefaultResponse<PageResponse<LikeStoryResponse>>(true, "Curtidas retornadas com sucesso", data);
    }

    private void likeStory(Story story, Profile profile) {
        LikeStoryId id = new LikeStoryId(profile.getId(), story.getId());
        LikeStory like = new LikeStory(id, profile, story);
        likeStoryRepository.save(like);

        String body = String.format("Seu story foi curtido por %s", profile.getUser().getHandle());
        this.pushNotificationService.sendNotification(profile, story.getProfile(), "Story Curtido", body, NotificationType.LIKE_IN_STORY);;
    }

    private void unlikeStory(Story story, Profile profile) {
        LikeStoryId id = new LikeStoryId(profile.getId(), story.getId());
        likeStoryRepository.findById(id).ifPresent(likeStoryRepository::delete);
    }
}
