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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;

@Service
public class ProfilePictureServiceImpl implements ProfilePictureService {

    @Autowired
    private ProfilePictureRepository profilePictureRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private ProfilePictureMapper profilePictureMapper;

    @Override
    @Transactional
    public DefaultResponse<ProfilePictureResponse> uploadProfilePicture(ProfilePictureRequest dto) {
        User user = this.authenticatedUserProvider.getAuthenticatedUser();

        Profile profile = this.profileRepository.findById(user.getProfile().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado"));

        if (profile.getProfilePicture() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já possui uma foto de perfil");
        }

        MultipartFile file = dto.file();

        String fileKey = this.tokenGenerator.generateToken(32);
        String fileName = file.getOriginalFilename();
        String mimeType = file.getContentType();

        ProfilePicture profilePicture = new ProfilePicture();

        System.out.println(profile.getFullName());

        profilePicture.setFileKey(fileKey);
        profilePicture.setFileName(fileName);
        profilePicture.setMimeType(mimeType);
        profilePicture.setProfile(profile);

        ProfilePicture profilePictureSaved = this.profilePictureRepository.save(profilePicture);

        try {
            InputStream inputStream = file.getInputStream();

            this.minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getMinioBucket())
                            .object(minioConfig.getProfilePicturesFolder() + fileKey)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(mimeType)
                            .build()
            );
        } catch (Exception error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falha ao ler o arquivo enviado, verifique se o arquivo é válido", error);
        }

        ProfilePictureResponse data = this.profilePictureMapper.toDTO(profilePictureSaved);

        return new DefaultResponse<ProfilePictureResponse>(true, "Foto de perfil salva com sucesso", data);
    }

    @Override
    public DefaultResponse<ProfilePictureResponse> findProfilePictureById(String profileId) {
        ProfilePicture profilePicture = this.profilePictureRepository.findById(UUIDConverter.toUUID(profileId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "O usuário não possuí foto de perfil"));

        ProfilePictureResponse data = this.profilePictureMapper.toDTO(profilePicture);

        return new DefaultResponse<ProfilePictureResponse>(true, "Foto de perfil retornada com sucesso", data);
    }

    @Override
    @Transactional
    public DefaultResponse<Void> deleteProfilePicture() {
        User user = this.authenticatedUserProvider.getAuthenticatedUser();

        Profile profile = this.profileRepository.findById(user.getProfile().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado"));

        if (profile.getProfilePicture() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "O usuário não possuí foto de perfil");
        }

        try {
            this.minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getMinioBucket())
                            .object(minioConfig.getProfilePicturesFolder() + profile.getProfilePicture().getFileKey())
                            .build()
            );
        } catch (Exception error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falha ao ler o arquivo enviado, verifique se o arquivo é válido", error);
        }

        profile.setProfilePicture(null);

        profileRepository.save(profile);

        return new DefaultResponse<Void>(true, "Foto de perfil excluida com sucesso", null);
    }
}
