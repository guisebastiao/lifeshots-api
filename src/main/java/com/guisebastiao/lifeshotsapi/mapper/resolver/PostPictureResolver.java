package com.guisebastiao.lifeshotsapi.mapper.resolver;

import com.guisebastiao.lifeshotsapi.config.MinioConfig;
import com.guisebastiao.lifeshotsapi.entity.PostPicture;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

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
                            .expiry(604800)
                            .build()
            );
        } catch (Exception error) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar URL da imagem da publicação", error);
        }
    }
}
