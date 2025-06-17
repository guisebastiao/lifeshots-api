package com.lifeshots.lifeshotsapi.mappers.resolvers;

import com.lifeshots.lifeshotsapi.exceptions.FailedUploadFileException;
import com.lifeshots.lifeshotsapi.models.ProfilePicture;
import com.lifeshots.lifeshotsapi.models.User;
import com.lifeshots.lifeshotsapi.repositories.ProfilePictureRepository;
import com.lifeshots.lifeshotsapi.security.AuthProvider;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProfilePictureResolver {

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private MinioClient minioClient;

    protected final String bucketName = "profile-pictures";

    @Named("getProfilePicture")
    public String getProfilePicture(ProfilePicture profilePicture) {
        String objectId = (profilePicture != null && profilePicture.getObjectId() != null)
                ? profilePicture.getObjectId()
                : "default-profile-picture.png";

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectId)
                            .expiry(604800)
                            .build()
            );
        } catch (Exception e) {
            throw new FailedUploadFileException("Um erro inesperado aconteceu ao processar a imagem de perfil");
        }
    }
}
