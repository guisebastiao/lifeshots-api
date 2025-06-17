package com.lifeshots.lifeshotsapi.services.impl;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.request.ProfilePictureRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.response.ProfilePictureResponseDTO;
import com.lifeshots.lifeshotsapi.exceptions.BadRequestException;
import com.lifeshots.lifeshotsapi.exceptions.EntityNotFoundException;
import com.lifeshots.lifeshotsapi.exceptions.FailedUploadFileException;
import com.lifeshots.lifeshotsapi.models.ProfilePicture;
import com.lifeshots.lifeshotsapi.models.User;
import com.lifeshots.lifeshotsapi.repositories.ProfilePictureRepository;
import com.lifeshots.lifeshotsapi.repositories.UserRepository;
import com.lifeshots.lifeshotsapi.security.AuthProvider;
import com.lifeshots.lifeshotsapi.services.ProfilePictureService;
import com.lifeshots.lifeshotsapi.utils.TokenGenerator;
import com.lifeshots.lifeshotsapi.utils.UUIDConverter;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import jakarta.transaction.Transactional;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Optional;

@Service
public class ProfilePictureServiceImpl implements ProfilePictureService {

    @Autowired
    private ProfilePictureRepository profilePictureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private TokenGenerator tokenGenerator;

    protected final String bucketName = "profile-pictures";

    @Override
    @Transactional
    public DefaultDTO uploadProfilePicture(ProfilePictureRequestDTO profilePictureRequestDTO) {
        User user = this.authProvider.getAuthenticatedUser();
        MultipartFile file = profilePictureRequestDTO.file();
        String objectId = tokenGenerator.generateToken();
        String contentType = file.getContentType();

        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setUser(user);
        profilePicture.setObjectId(objectId);

        this.profilePictureRepository.save(profilePicture);

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
            throw new FailedUploadFileException("Um erro inesperado aconteceu ao enviar a imagem de perfil");
        }

        return new DefaultDTO("Sua imagem de perfil foi salva com sucesso", Boolean.TRUE, null, null, null);
    }

    @Override
    public DefaultDTO findProfilePictureToUser(String userId) {
        this.userRepository.findById(UUIDConverter.toUUID(userId))
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Optional<ProfilePicture> profilePicture = this.profilePictureRepository.findByUserId(UUIDConverter.toUUID(userId));
        String objectId = profilePicture.map(ProfilePicture::getObjectId).orElse("default-profile-picture.png");

        try {
            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectId)
                            .expiry(604800)
                            .build()
            );

            ProfilePictureResponseDTO  profilePictureResponseDTO = new ProfilePictureResponseDTO(presignedUrl);

            return new DefaultDTO("Imagem perfil encontrada com sucesso", Boolean.TRUE, profilePictureResponseDTO, null, null);
        } catch (Exception e) {
            throw new FailedUploadFileException("Um erro inesperado aconteceu ao processar a imagem de perfil");
        }
    }

    @Override
    @Transactional
    public DefaultDTO deleteProfilePicture() {
        User user = this.authProvider.getAuthenticatedUser();

        ProfilePicture profilePicture = this.profilePictureRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Você não possui nenhuma imagem de perfil"));

        try {
            this.minioClient.removeObject(
                    RemoveObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object(profilePicture.getObjectId())
                            .build()
            );
        } catch (Exception e) {
            throw new FailedUploadFileException("Um erro inesperado aconteceu ao deletar a imagem de perfil");
        }

        user.setProfilePicture(null);
        this.userRepository.save(user);
        this.profilePictureRepository.delete(profilePicture);

        return new DefaultDTO("Imagem perfil deletada com sucesso", Boolean.TRUE, null, null, null);
    }
}
