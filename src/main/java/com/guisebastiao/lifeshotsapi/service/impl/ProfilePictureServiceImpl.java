package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.config.MinioConfig;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.ProfilePictureRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfilePictureResponse;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.ProfilePicture;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.exception.*;
import com.guisebastiao.lifeshotsapi.mapper.ProfilePictureMapper;
import com.guisebastiao.lifeshotsapi.repository.ProfilePictureRepository;
import com.guisebastiao.lifeshotsapi.repository.ProfileRepository;
import com.guisebastiao.lifeshotsapi.security.provider.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.ProfilePictureService;
import com.guisebastiao.lifeshotsapi.util.TokenGenerator;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

@Service
public class ProfilePictureServiceImpl implements ProfilePictureService {

    private final ProfilePictureRepository profilePictureRepository;
    private final ProfileRepository profileRepository;
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;
    private final TokenGenerator tokenGenerator;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ProfilePictureMapper profilePictureMapper;
    private final UUIDConverter uuidConverter;

    public ProfilePictureServiceImpl(ProfilePictureRepository profilePictureRepository, ProfileRepository profileRepository, MinioClient minioClient, MinioConfig minioConfig, TokenGenerator tokenGenerator, AuthenticatedUserProvider authenticatedUserProvider, ProfilePictureMapper profilePictureMapper, UUIDConverter uuidConverter) {
        this.profilePictureRepository = profilePictureRepository;
        this.profileRepository = profileRepository;
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
        this.tokenGenerator = tokenGenerator;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.profilePictureMapper = profilePictureMapper;
        this.uuidConverter = uuidConverter;
    }

    @Override
    @Transactional
    public DefaultResponse<ProfilePictureResponse> uploadProfilePicture(ProfilePictureRequest dto) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        Profile profile = profileRepository.findById(user.getProfile().getId())
                .orElseThrow(() -> new NotFoundException("services.profile-picture-service.methods.upload-profile-picture.not-found "));

        if (profile.getProfilePicture() != null) {
            throw new ConflictException("services.profile-picture-service.methods.upload-profile-picture.conflict");
        }

        MultipartFile file = dto.file();

        String fileKey = tokenGenerator.generateToken(32);
        String mimeType = file.getContentType();

        ProfilePicture profilePicture = ProfilePicture.builder()
                .fileKey(fileKey)
                .fileName(file.getOriginalFilename())
                .mimeType(mimeType)
                .profile(profile)
                .build();

        ProfilePicture profilePictureSaved = profilePictureRepository.save(profilePicture);

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getMinioBucket())
                            .object(minioConfig.getProfilePicturesFolder() + fileKey)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(mimeType)
                            .build()
            );
        } catch (Exception ignored) {
            throw new FailedDependencyException();
        }

        return DefaultResponse.success(profilePictureMapper.toDTO(profilePictureSaved));
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<ProfilePictureResponse> findProfilePictureById(String profileId) {
        ProfilePicture profilePicture = profilePictureRepository.findById(uuidConverter.toUUID(profileId))
                .orElseThrow(() -> new NotFoundException("services.profile-picture-service.methods.find-profile-picture-by-id.not-found"));

        return DefaultResponse.success(profilePictureMapper.toDTO(profilePicture));
    }

    @Override
    @Transactional
    public DefaultResponse<Void> deleteProfilePicture() {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        Profile profile = profileRepository.findById(user.getProfile().getId())
                .orElseThrow(() -> new NotFoundException("services.profile-picture-service.methods.delete-profile-picture.profile-not-found"));

        if (profile.getProfilePicture() == null) {
            throw new NotFoundException("services.profile-picture-service.methods.delete-profile-picture.picture-not-found");
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getMinioBucket())
                            .object(minioConfig.getProfilePicturesFolder() + profile.getProfilePicture().getFileKey())
                            .build()
            );
        } catch (Exception error) {
            throw new BadRequestException("");
        }

        profile.setProfilePicture(null);

        profileRepository.save(profile);

        return DefaultResponse.success();
    }
}
