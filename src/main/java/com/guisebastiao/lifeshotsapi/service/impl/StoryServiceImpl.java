package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.config.MinioConfig;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.StoryRequest;
import com.guisebastiao.lifeshotsapi.dto.request.StoryUpdateRequest;
import com.guisebastiao.lifeshotsapi.dto.response.StoryResponse;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.Story;
import com.guisebastiao.lifeshotsapi.entity.StoryPicture;
import com.guisebastiao.lifeshotsapi.mapper.StoryMapper;
import com.guisebastiao.lifeshotsapi.repository.ProfileRepository;
import com.guisebastiao.lifeshotsapi.repository.StoryPictureRepository;
import com.guisebastiao.lifeshotsapi.repository.StoryRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.StoryService;
import com.guisebastiao.lifeshotsapi.util.TokenGenerator;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;
    private final StoryPictureRepository storyPictureRepository;
    private final ProfileRepository profileRepository;
    private final StoryMapper storyMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final TokenGenerator tokenGenerator;
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;
    private final MessageSource messageSource;
    private final UUIDConverter uuidConverter;

    public StoryServiceImpl(StoryRepository storyRepository, StoryPictureRepository storyPictureRepository, ProfileRepository profileRepository, StoryMapper storyMapper, AuthenticatedUserProvider authenticatedUserProvider, TokenGenerator tokenGenerator, MinioClient minioClient, MinioConfig minioConfig, MessageSource messageSource, UUIDConverter uuidConverter) {
        this.storyRepository = storyRepository;
        this.storyPictureRepository = storyPictureRepository;
        this.profileRepository = profileRepository;
        this.storyMapper = storyMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.tokenGenerator = tokenGenerator;
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
        this.messageSource = messageSource;
        this.uuidConverter = uuidConverter;
    }

    @Override
    @Transactional
    public DefaultResponse<StoryResponse> createStory(StoryRequest dto) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        if (storyRepository.countStoriesByProfile(profile) > 15) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getMessage("services.story-service.methods.create-story.limit-bad-request"));
        }

        Instant expiresAt = LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC);

        Story story = storyMapper.toEntity(dto);
        story.setProfile(profile);
        story.setExpiresAt(expiresAt);

        Story savedStory = storyRepository.save(story);

        String fileKey = tokenGenerator.generateToken(32);
        String mimeType = dto.file().getContentType();
        String fileName = dto.file().getOriginalFilename();

        try (InputStream inputStream = dto.file().getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getMinioBucket())
                            .object(minioConfig.getStoryPicturesFolder() + fileKey)
                            .stream(inputStream, dto.file().getSize(), -1)
                            .contentType(mimeType)
                            .build()
            );
        } catch (Exception error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getMessage("services.story-service.methods.create-story.file-bad-request"), error);
        }

        StoryPicture storyPicture = new StoryPicture();
        storyPicture.setStory(savedStory);
        storyPicture.setFileKey(fileKey);
        storyPicture.setFileName(fileName);
        storyPicture.setMimeType(mimeType);

        storyPictureRepository.save(storyPicture);

        savedStory.setStoryPicture(storyPicture);

        return DefaultResponse.success(storyMapper.toDTO(savedStory));
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<StoryResponse> findStoryById(String storyId) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Story story = storyRepository.findByIdAndNotDeleted(uuidConverter.toUUID(storyId)).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.story-service.methods.find-story-by-id.not-found")));

        boolean mutualFollow = profileRepository.profilesFollowEachOther(story.getProfile(), profile);

        if (story.getProfile().isPrivate() && !mutualFollow && !profile.getId().equals(story.getProfile().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, getMessage("services.story-service.methods.find-story-by-id.forbidden"));
        }

        return DefaultResponse.success(storyMapper.toDTO(story));
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<List<StoryResponse>> findStoriesByAuthUser() {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        List<Story> stories = storyRepository.findAllStoriesByProfile(profile);

        List<StoryResponse> data = stories.stream().map(storyMapper::toDTO).toList();

        return DefaultResponse.success(data);
    }

    @Override
    @Transactional
    public DefaultResponse<StoryResponse> updateStory(String storyId, StoryUpdateRequest dto) {
        Story story = findStoryAndBelongsToTheProfile(storyId);

        story.setCaption(dto.caption());

        Story savedStory = storyRepository.save(story);

        return DefaultResponse.success(storyMapper.toDTO(savedStory));
    }

    @Override
    @Transactional
    public DefaultResponse<Void> deleteStory(String storyId) {
        Story story = findStoryAndBelongsToTheProfile(storyId);

        story.setDeleted(true);
        storyRepository.save(story);

        return DefaultResponse.success();
    }

    private Story findStoryAndBelongsToTheProfile(String storyId) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile().getUser().getProfile();

        Story story = storyRepository.findById(uuidConverter.toUUID(storyId)).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.story-service.methods.find-story-and-belongs-to-the-profile.not-found")));

        if (!story.getProfile().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, getMessage("services.story-service.methods.find-story-and-belongs-to-the-profile.forbidden"));
        }

        return story;
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
