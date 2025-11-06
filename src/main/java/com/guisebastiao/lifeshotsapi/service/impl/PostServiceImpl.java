package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.config.MinioConfig;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.PostRequest;
import com.guisebastiao.lifeshotsapi.dto.request.PostUpdateRequest;
import com.guisebastiao.lifeshotsapi.dto.response.PostResponse;
import com.guisebastiao.lifeshotsapi.entity.Post;
import com.guisebastiao.lifeshotsapi.entity.PostPicture;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.mapper.PostMapper;
import com.guisebastiao.lifeshotsapi.repository.PostPictureRepository;
import com.guisebastiao.lifeshotsapi.repository.PostRepository;
import com.guisebastiao.lifeshotsapi.repository.ProfileRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.PostService;
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
import java.util.List;
import java.util.UUID;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostPictureRepository postPictureRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    @Override
    @Transactional
    public DefaultResponse<PostResponse> createPost(PostRequest dto) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = this.postMapper.toEntity(dto);
        post.setProfile(profile);

        Post savedPost = this.postRepository.save(post);

        List<PostPicture> postPictures = this.generatePostPictures(dto.files(), post);

        savedPost.setPostPictures(postPictures);

        profile.setPostsCount(profile.getPostsCount() + 1);
        profileRepository.save(profile);

        PostResponse data = this.postMapper.toDTO(savedPost);

        return new DefaultResponse<PostResponse>(true, "Publicação criada com sucesso", data);
    }

    @Override
    public DefaultResponse<PostResponse> findPostById(String postId) {
        Profile profileAuth = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = this.postRepository.findByIdAndNotDeleted(UUIDConverter.toUUID(postId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicação não encontrada"));

        boolean mutualFollow = this.profileRepository.profilesFollowEachOther(post.getProfile(), profileAuth);

        if (post.getProfile().isPrivate() && !mutualFollow && !profileAuth.getId().equals(post.getProfile().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Perfil privado, você não tem permissão para verificar essa publicação");
        }

        PostResponse data = this.postMapper.toDTO(post);

        return new DefaultResponse<PostResponse>(true, "Publicação retornada com sucesso", data);
    }

    @Override
    @Transactional
    public DefaultResponse<PostResponse> updatePost(String postId, PostUpdateRequest dto) {
        Post post = this.findPostAndBelongsToTheProfile(postId);

        List<UUID> removeFiles = dto.removeFiles() != null ? dto.removeFiles() : List.of();
        List<MultipartFile> newFiles = dto.newFiles() != null ? dto.newFiles() : List.of();

        int totalPictures = post.getPostPictures().size() - removeFiles.size() + newFiles.size();

        if (totalPictures > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A publicação não pode ter mais de dez imagens");
        }

        if (totalPictures <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A publicação tem que possuir pelo menos uma imagem");
        }

        if (!removeFiles.isEmpty()) {
            List<PostPicture> postPictures = this.postPictureRepository.findAllById(removeFiles);

            postPictures.forEach(postPicture -> {
                try {
                    this.minioClient.removeObject(
                            RemoveObjectArgs.builder()
                                    .bucket(this.minioConfig.getMinioBucket())
                                    .object(minioConfig.getPostPicturesFolder() + postPicture.getFileKey())
                                    .build()
                    );
                } catch (Exception error) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao deletar o arquivo", error);
                }
            });

            this.postPictureRepository.deleteAll(postPictures);
            post.getPostPictures().removeAll(postPictures);
        }

        if (!newFiles.isEmpty()) {
            List<PostPicture> postPictures = this.generatePostPictures(newFiles, post);
            this.postPictureRepository.saveAll(postPictures);
            post.getPostPictures().addAll(postPictures);
        }

        this.postMapper.updatePost(dto, post);
        this.postRepository.save(post);

        PostResponse data = this.postMapper.toDTO(post);

        return new DefaultResponse<PostResponse>(true, "Publicação editada com sucesso", data);
    }

    @Override
    @Transactional
    public DefaultResponse<Void> deletePost(String postId) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = this.findPostAndBelongsToTheProfile(postId);
        post.setDeleted(true);

        this.postRepository.save(post);

        profile.setPostsCount(profile.getPostsCount() - 1);
        profileRepository.save(profile);

        return new DefaultResponse<Void>(true, "Publicação excluida com sucesso", null);
    }

    private Post findPostAndBelongsToTheProfile(String postId) {
        Profile profileAuth = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = this.postRepository.findById(UUIDConverter.toUUID(postId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicação não encontrada"));

        if (!profileAuth.getId().equals(post.getProfile().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para manipular essa publicação");
        }

        return post;
    }

    private List<PostPicture> generatePostPictures(List<MultipartFile> files, Post post) {
        return files
                .stream()
                .map((file) -> {
                    String fileKey = this.tokenGenerator.generateToken(32);
                    String mimeType = file.getContentType();
                    String fileName = file.getOriginalFilename();

                    try (InputStream inputStream = file.getInputStream()) {
                        this.minioClient.putObject(
                                PutObjectArgs.builder()
                                        .bucket(minioConfig.getMinioBucket())
                                        .object(minioConfig.getPostPicturesFolder() + fileKey)
                                        .stream(inputStream, file.getSize(), -1)
                                        .contentType(mimeType)
                                        .build()
                        );
                    } catch (Exception error) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falha ao ler o arquivo enviado, verifique se o arquivo é válido", error);
                    }

                    PostPicture picture = new PostPicture();
                    picture.setPost(post);
                    picture.setFileKey(fileKey);
                    picture.setFileName(fileName);
                    picture.setMimeType(mimeType);

                    return picture;
                })
                .toList();
    };
}
