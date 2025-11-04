package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.config.MinioConfig;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.StoryRequest;
import com.guisebastiao.lifeshotsapi.dto.response.StoryResponse;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.Story;
import com.guisebastiao.lifeshotsapi.entity.StoryPicture;
import com.guisebastiao.lifeshotsapi.mapper.StoryMapper;
import com.guisebastiao.lifeshotsapi.repository.StoryPictureRepository;
import com.guisebastiao.lifeshotsapi.repository.StoryRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.StoryService;
import com.guisebastiao.lifeshotsapi.util.TokenGenerator;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Service
public class StoryServiceImpl implements StoryService {

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private StoryPictureRepository storyPictureRepository;

    @Autowired
    private StoryMapper storyMapper;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    @Override
    @Transactional
    public DefaultResponse<StoryResponse> createStory(StoryRequest dto) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile().getUser().getProfile();

        Instant expiresAt = LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC);

        Story story = this.storyMapper.toEntity(dto);
        story.setProfile(profile);
        story.setExpiresAt(expiresAt);

        Story savedStory = this.storyRepository.save(story);

        String fileKey = this.tokenGenerator.generateToken(32);
        String mimeType = dto.file().getContentType();
        String fileName = dto.file().getOriginalFilename();

        try {
            InputStream inputStream = dto.file().getInputStream();

            this.minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getMinioBucket())
                            .object(minioConfig.getStoryPicturesFolder() + fileKey)
                            .stream(inputStream, dto.file().getSize(), -1)
                            .contentType(mimeType)
                            .build()
            );
        } catch (Exception error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falha ao ler o arquivo enviado, verifique se o arquivo é válido", error);
        }

        StoryPicture storyPicture = new StoryPicture();
        storyPicture.setStory(savedStory);
        storyPicture.setFileKey(fileKey);
        storyPicture.setFileName(fileName);
        storyPicture.setMimeType(mimeType);

        this.storyPictureRepository.save(storyPicture);

        savedStory.setStoryPicture(storyPicture);

        StoryResponse data = this.storyMapper.toDTO(savedStory);

        return new DefaultResponse<StoryResponse>(true, "Story criado com sucesso", data);
    }

    @Override
    public DefaultResponse<StoryResponse> findStoryById(String storyId) {
        Story story = this.storyRepository.findByIdAndNotDeleted(UUIDConverter.toUUID(storyId)).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Story não encontrado"));

        // VERIFICAR SE O PERFIL É PRIVADO E OS USUARIOS NÃO SE SEGUEM RETORNAR 403.

        StoryResponse data = this.storyMapper.toDTO(story);

        return new DefaultResponse<StoryResponse>(true, "Story retornado com sucesso", data);
    }

    @Override
    @Transactional
    public DefaultResponse<StoryResponse> updateStory(String storyId, StoryRequest dto) {
        Story story = this.findStoryAndBelongsToTheProfile(storyId);

        this.storyMapper.updateStory(dto, story);

        Story savedStory = this.storyRepository.save(story);

        StoryResponse data = this.storyMapper.toDTO(savedStory);

        return new DefaultResponse<StoryResponse>(true, "Story editado com sucesso", data);
    }

    @Override
    @Transactional
    public DefaultResponse<Void> deleteStory(String storyId) {
        Story story = this.findStoryAndBelongsToTheProfile(storyId);

        story.setDeleted(true);
        this.storyRepository.save(story);

        return new DefaultResponse<Void>(true, "Story deletado com sucesso", null);
    }

    private Story findStoryAndBelongsToTheProfile(String storyId) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile().getUser().getProfile();

        Story story = this.storyRepository.findById(UUIDConverter.toUUID(storyId)).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Story não encontrado"));

        if (!story.getProfile().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão de editar esse story");
        }

        return story;
    }
}
