package com.guisebastiao.lifeshotsapi.mapper.resolver;

import com.guisebastiao.lifeshotsapi.config.MinioConfig;
import com.guisebastiao.lifeshotsapi.entity.PostPicture;
import com.guisebastiao.lifeshotsapi.exception.FailedDependencyException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class PostPictureResolver {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public PostPictureResolver(MinioClient minioClient, MinioConfig minioConfig) {
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
    }

    @Named("getPostPictureUrl")
    public String getPostPictureUrl(PostPicture postPicture) {
        if (postPicture == null) return null;

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioConfig.getMinioBucket())
                            .object(minioConfig.getPostPicturesFolder() + postPicture.getFileKey())
                            .expiry(43200)
                            .build()
            );
        } catch (Exception error) {
            throw new FailedDependencyException();
        }
    }
}
