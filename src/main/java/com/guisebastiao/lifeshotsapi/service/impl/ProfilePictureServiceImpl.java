package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.config.MinioConfig;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.ProfilePictureRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfilePictureResponse;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.ProfilePicture;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.mapper.ProfilePictureMapper;
import com.guisebastiao.lifeshotsapi.repository.ProfilePictureRepository;
import com.guisebastiao.lifeshotsapi.repository.ProfileRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.ProfilePictureService;
import com.guisebastiao.lifeshotsapi.util.TokenGenerator;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
    private final MessageSource messageSource;
    private final UUIDConverter uuidConverter;

    public ProfilePictureServiceImpl(ProfilePictureRepository profilePictureRepository, ProfileRepository profileRepository, MinioClient minioClient, MinioConfig minioConfig, TokenGenerator tokenGenerator, AuthenticatedUserProvider authenticatedUserProvider, ProfilePictureMapper profilePictureMapper, MessageSource messageSource, UUIDConverter uuidConverter) {
        this.profilePictureRepository = profilePictureRepository;
        this.profileRepository = profileRepository;
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
        this.tokenGenerator = tokenGenerator;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.profilePictureMapper = profilePictureMapper;
        this.messageSource = messageSource;
        this.uuidConverter = uuidConverter;
    }

    @Override
    @Transactional
    public DefaultResponse<ProfilePictureResponse> uploadProfilePicture(ProfilePictureRequest dto) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        Profile profile = profileRepository.findById(user.getProfile().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.profile-picture-service.methods.upload-profile-picture.not-found")));

        if (profile.getProfilePicture() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, getMessage("services.profile-picture-service.methods.upload-profile-picture.conflict"));
        }

        MultipartFile file = dto.file();

        String fileKey = tokenGenerator.generateToken(32);
        String fileName = file.getOriginalFilename();
        String mimeType = file.getContentType();

        ProfilePicture profilePicture = new ProfilePicture();

        System.out.println(profile.getFullName());

        profilePicture.setFileKey(fileKey);
        profilePicture.setFileName(fileName);
        profilePicture.setMimeType(mimeType);
        profilePicture.setProfile(profile);

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
        } catch (Exception error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getMessage("services.profile-picture-service.methods.upload-profile-picture.bad-request"), error);
        }

        return DefaultResponse.success(profilePictureMapper.toDTO(profilePictureSaved));
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<ProfilePictureResponse> findProfilePictureById(String profileId) {
        ProfilePicture profilePicture = profilePictureRepository.findById(uuidConverter.toUUID(profileId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.profile-picture-service.methods.find-profile-picture-by-id.not-found")));

        return DefaultResponse.success(profilePictureMapper.toDTO(profilePicture));
    }

    @Override
    @Transactional
    public DefaultResponse<Void> deleteProfilePicture() {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        Profile profile = profileRepository.findById(user.getProfile().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.profile-picture-service.methods.delete-profile-picture.profile-not-found")));

        if (profile.getProfilePicture() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("services.profile-picture-service.methods.delete-profile-picture.picture-not-found"));
        }

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getMinioBucket())
                            .object(minioConfig.getProfilePicturesFolder() + profile.getProfilePicture().getFileKey())
                            .build()
            );
        } catch (Exception error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getMessage("services.profile-picture-service.methods.delete-profile-picture.bad-request"), error);
        }

        profile.setProfilePicture(null);

        profileRepository.save(profile);

        return DefaultResponse.success();
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
