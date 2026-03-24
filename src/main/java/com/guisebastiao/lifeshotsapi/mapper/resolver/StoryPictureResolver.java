package com.guisebastiao.lifeshotsapi.mapper.resolver;

import com.guisebastiao.lifeshotsapi.config.MinioConfig;
import com.guisebastiao.lifeshotsapi.entity.StoryPicture;
import com.guisebastiao.lifeshotsapi.exception.FailedDependencyException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

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
                            .expiry(43200)
                            .build()
            );
        } catch (Exception error) {
            throw new FailedDependencyException();
        }
    }
}
