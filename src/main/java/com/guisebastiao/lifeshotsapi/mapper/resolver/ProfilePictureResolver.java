package com.guisebastiao.lifeshotsapi.mapper.resolver;

import com.guisebastiao.lifeshotsapi.config.MinioConfig;
import com.guisebastiao.lifeshotsapi.entity.ProfilePicture;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.mapstruct.Named;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ProfilePictureResolver {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public ProfilePictureResolver(MinioClient minioClient, MinioConfig minioConfig) {
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
    }

    @Named("getProfilePictureUrl")
    public String getProfilePictureUrl(ProfilePicture profilePicture) {

        System.out.println(profilePicture.getFileName() + " - " +  profilePicture.getFileKey());

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioConfig.getMinioBucket())
                            .object(minioConfig.getProfilePicturesFolder() + profilePicture.getFileKey())
                            .expiry(604800)
                            .build()
            );
        } catch (Exception error) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar URL da imagem de perfil", error);
        }
    }
}
