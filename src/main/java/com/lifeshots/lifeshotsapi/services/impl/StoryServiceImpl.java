package com.lifeshots.lifeshotsapi.services.impl;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.PagingDTO;
import com.lifeshots.lifeshotsapi.dtos.request.StoryCreateRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.request.StoryUpdateRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.response.StoryResponseDTO;
import com.lifeshots.lifeshotsapi.exceptions.EntityNotFoundException;
import com.lifeshots.lifeshotsapi.exceptions.FailedUploadFileException;
import com.lifeshots.lifeshotsapi.exceptions.UnauthorizedException;
import com.lifeshots.lifeshotsapi.mappers.StoryMapper;
import com.lifeshots.lifeshotsapi.models.Story;
import com.lifeshots.lifeshotsapi.models.StoryPicture;
import com.lifeshots.lifeshotsapi.models.User;
import com.lifeshots.lifeshotsapi.repositories.StoryPictureRepository;
import com.lifeshots.lifeshotsapi.repositories.StoryRepository;
import com.lifeshots.lifeshotsapi.security.AuthProvider;
import com.lifeshots.lifeshotsapi.services.StoryService;
import com.lifeshots.lifeshotsapi.utils.TokenGenerator;
import com.lifeshots.lifeshotsapi.utils.UUIDConverter;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StoryServiceImpl implements StoryService {

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private StoryPictureRepository storyPictureRepository;

    @Autowired
    private StoryMapper storyMapper;

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private MinioClient minioClient;

    protected final String bucketName = "story-pictures";

    @Override
    @Transactional
    public DefaultDTO createStory(StoryCreateRequestDTO storyCreateRequestDTO) {
        User user = this.authProvider.getAuthenticatedUser();

        Story story = storyMapper.toEntity(storyCreateRequestDTO);
        story.setExpiresAt(this.generateExpirateDate());
        story.setUser(user);

        Story savedStory = this.storyRepository.save(story);

        MultipartFile file = storyCreateRequestDTO.file();
        String objectId = tokenGenerator.generateToken();
        String contentType = file.getContentType();

        StoryPicture storyPicture = new StoryPicture();
        storyPicture.setStory(savedStory);
        storyPicture.setObjectId(objectId);

        this.storyPictureRepository.save(storyPicture);

        try {
            InputStream inputStream = file.getInputStream();

            this.minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(this.bucketName)
                            .object(objectId)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            throw new FailedUploadFileException("Um erro inesperado aconteceu ao enviar a imagem do story");
        }

        return new DefaultDTO("Story criado com sucesso", Boolean.TRUE, null, null, null);
    }

    @Override
    public DefaultDTO findStoryById(String storyId) {
        Story story = this.getStoryById(storyId);
        StoryResponseDTO storyResponseDTO = this.storyMapper.toDTO(story);
        return new DefaultDTO("Story encontrado com sucesso", Boolean.TRUE, storyResponseDTO, null, null);
    }

    @Override
    public DefaultDTO findAllStoriesBelongsFollowers(int offset, int limit) {
        User user = this.authProvider.getAuthenticatedUser();

        Pageable pageable = PageRequest.of(offset, limit);
        Page<Story> resultPage = this.storyRepository.findAllStoriesByBelongsFollowers(user.getId(), pageable);

        PagingDTO pagingDTO = new PagingDTO(resultPage.getTotalElements(), resultPage.getTotalPages(), offset, limit);

        List<StoryResponseDTO> data = resultPage.stream()
                .map(this.storyMapper::toDTO)
                .toList();

        return new DefaultDTO("Os stories foram buscados com sucesso", Boolean.TRUE, data, null, pagingDTO);
    }

    @Override
    public DefaultDTO updateStory(String storyId, StoryUpdateRequestDTO storyUpdateRequestDTO) {
        Story story = this.getStoryById(storyId);
        this.checkCreator(story.getUser());

        story.setContent(storyUpdateRequestDTO.content());

        this.storyRepository.save(story);

        return new DefaultDTO("Story atualizado com sucesso", Boolean.TRUE, null, null, null);
    }

    @Override
    public DefaultDTO deleteStory(String storyId) {
        Story story = this.getStoryById(storyId);
        this.checkCreator(story.getUser());

        try {
            this.minioClient.removeObject(
                    RemoveObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object(story.getStoryPicture().getObjectId())
                            .build()
            );
        } catch (Exception e) {
            throw new FailedUploadFileException("Um erro inesperado aconteceu ao deletar a imagem do story");
        }

        this.storyRepository.delete(story);

        return new DefaultDTO("Story excluido com sucesso", Boolean.TRUE, null, null, null);
    }

    private LocalDateTime generateExpirateDate() {
        return  LocalDateTime.now().plusDays(1);
    }

    private Story getStoryById(String storyId) {
        return this.storyRepository.findById(UUIDConverter.toUUID(storyId))
                .orElseThrow(() -> new EntityNotFoundException("O story não foi encontrado"));
    }

    private void checkCreator(User user) {
        User authenticatedUser = this.authProvider.getAuthenticatedUser();

        if(!authenticatedUser.getId().equals(user.getId())) {
            throw new UnauthorizedException("Você não tem permissão de modificar esse story");
        }
    }
}
