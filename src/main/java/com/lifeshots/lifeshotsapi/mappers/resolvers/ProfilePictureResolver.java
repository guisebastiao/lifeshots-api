package com.lifeshots.lifeshotsapi.mappers.resolvers;

import com.lifeshots.lifeshotsapi.exceptions.FailedUploadFileException;
import com.lifeshots.lifeshotsapi.models.ProfilePicture;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfilePictureResolver {

    @Autowired
    private MinioClient minioClient;

    protected final String bucketName = "profile-pictures";

    @Named("getProfilePicture")
    public String getProfilePicture(ProfilePicture profilePicture) {
        String objectId = profilePicture.getObjectId();

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
