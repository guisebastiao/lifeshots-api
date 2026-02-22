package com.guisebastiao.lifeshotsapi.mapper.resolver;

import com.guisebastiao.lifeshotsapi.config.MinioConfig;
import com.guisebastiao.lifeshotsapi.entity.StoryPicture;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.mapstruct.Named;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class StoryPictureResolver {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public StoryPictureResolver(MinioClient minioClient, MinioConfig minioConfig) {
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
    }

    @Named("getStoryPictureUrl")
    public String getStoryPictureUrl(StoryPicture storyPicture) {
        if (storyPicture == null) return null;

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioConfig.getMinioBucket())
                            .object(minioConfig.getStoryPicturesFolder() + storyPicture.getFileKey())
                            .expiry(604800)
                            .build()
            );
        } catch (Exception error) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar URL da imagem do story", error);
        }
    }
}
