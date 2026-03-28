package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.config.MinioConfig;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.PostRequest;
import com.guisebastiao.lifeshotsapi.dto.request.PostUpdateRequest;
import com.guisebastiao.lifeshotsapi.dto.response.FieldErrorResponse;
import com.guisebastiao.lifeshotsapi.dto.response.PostResponse;
import com.guisebastiao.lifeshotsapi.entity.Post;
import com.guisebastiao.lifeshotsapi.entity.PostPicture;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.exception.*;
import com.guisebastiao.lifeshotsapi.mapper.PostMapper;
import com.guisebastiao.lifeshotsapi.repository.PostRepository;
import com.guisebastiao.lifeshotsapi.repository.ProfileRepository;
import com.guisebastiao.lifeshotsapi.security.provider.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.PostService;
import com.guisebastiao.lifeshotsapi.util.TokenGenerator;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final PostMapper postMapper;
    private final TokenGenerator tokenGenerator;
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;
    private final UUIDConverter uuidConverter;

    public PostServiceImpl(PostRepository postRepository, ProfileRepository profileRepository, AuthenticatedUserProvider authenticatedUserProvider, PostMapper postMapper, TokenGenerator tokenGenerator, MinioClient minioClient, MinioConfig minioConfig, UUIDConverter uuidConverter) {
        this.postRepository = postRepository;
        this.profileRepository = profileRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.postMapper = postMapper;
        this.tokenGenerator = tokenGenerator;
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
        this.uuidConverter = uuidConverter;
    }

    @Override
    @Transactional
    public DefaultResponse<PostResponse> createPost(PostRequest dto) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = postMapper.toEntity(dto);
        post.setProfile(profile);

        Post savedPost = postRepository.save(post);

        List<PostPicture> postPictures = generatePostPictures(dto.files(), post);

        savedPost.setPostPictures(postPictures);

        profile.setPostsCount(profile.getPostsCount() + 1);
        profileRepository.save(profile);

        return DefaultResponse.success(postMapper.toDTO(savedPost));
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<PostResponse> findPostById(String postId) {
        Profile profileAuth = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = postRepository.findByIdAndNotDeleted(uuidConverter.toUUID(postId))
                .orElseThrow(() -> new NotFoundException("services.post-service.methods.find-post-by-id.not-found"));

        boolean mutualFollow = profileRepository.profilesFollowEachOther(post.getProfile(), profileAuth);

        if (post.getProfile().isPrivate() && !mutualFollow && !profileAuth.getId().equals(post.getProfile().getId())) {
            throw new PrivateProfileException();
        }

        return DefaultResponse.success(postMapper.toDTO(post));
    }

    @Override
    @Transactional
    public DefaultResponse<PostResponse> updatePost(String postId, PostUpdateRequest dto) {
        Post post = findPostAndBelongsToTheProfile(postId);

        List<UUID> removeFiles = dto.removeFiles() != null ? dto.removeFiles() : List.of();
        List<MultipartFile> newFiles = dto.newFiles() != null ? dto.newFiles() : List.of();

        int totalPictures = post.getPostPictures().size() - removeFiles.size() + newFiles.size();

        if (totalPictures > 10) {
            List<FieldErrorResponse> errors = List.of(new FieldErrorResponse("newFiles", "services.post-service.methods.update-post.bad-request-max-pictures"));
            throw new ValidationException(errors);
        }

        if (totalPictures <= 0) {
            List<FieldErrorResponse> errors = List.of(new FieldErrorResponse("removeFiles", "services.post-service.methods.update-post.bad-request-min-pictures"));
            throw new ValidationException(errors);
        }

        if (!removeFiles.isEmpty()) {
            List<PostPicture> postPictures = post.getPostPictures().stream()
                    .filter(pp -> removeFiles.contains(pp.getId()))
                    .toList();

            if (postPictures.size() != removeFiles.size()) {
                throw new BadRequestException("services.post-service.methods.update-post.bad-request-invalid-pictures");
            }

            postPictures.forEach(postPicture -> {
                try {
                    minioClient.removeObject(
                            RemoveObjectArgs.builder()
                                    .bucket(minioConfig.getMinioBucket())
                                    .object(minioConfig.getPostPicturesFolder() + postPicture.getFileKey())
                                    .build()
                    );
                } catch (Exception ignored) {
                    throw new FailedDependencyException();
                }
            });

            post.getPostPictures().removeAll(postPictures);
        }

        if (!newFiles.isEmpty()) {
            List<PostPicture> postPictures = generatePostPictures(newFiles, post);
            post.getPostPictures().addAll(postPictures);
        }

        postMapper.updatePost(dto, post);
        postRepository.save(post);

        return DefaultResponse.success(postMapper.toDTO(post));
    }

    @Override
    @Transactional
    public DefaultResponse<Void> deletePost(String postId) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = findPostAndBelongsToTheProfile(postId);
        post.setDeleted(true);

        postRepository.save(post);

        profile.setPostsCount(profile.getPostsCount() - 1);
        profileRepository.save(profile);

        return DefaultResponse.success();
    }

    private Post findPostAndBelongsToTheProfile(String postId) {
        Profile profileAuth = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Post post = postRepository.findByIdAndNotDeleted(uuidConverter.toUUID(postId))
                .orElseThrow(() -> new NotFoundException("services.post-service.methods.find-post-and-belongs-to-the-profile.not-found"));

        if (!profileAuth.getId().equals(post.getProfile().getId())) {
            throw new AccessDeniedException("services.post-service.methods.find-post-and-belongs-to-the-profile.forbidden");
        }

        return post;
    }

    private List<PostPicture> generatePostPictures(List<MultipartFile> files, Post post) {
        return files
                .stream()
                .map((file) -> {
                    String fileKey = tokenGenerator.generateToken(32);
                    String mimeType = file.getContentType();
                    String fileName = file.getOriginalFilename();

                    try (InputStream inputStream = file.getInputStream()) {
                        minioClient.putObject(
                                PutObjectArgs.builder()
                                        .bucket(minioConfig.getMinioBucket())
                                        .object(minioConfig.getPostPicturesFolder() + fileKey)
                                        .stream(inputStream, file.getSize(), -1)
                                        .contentType(mimeType)
                                        .build()
                        );
                    } catch (Exception ignored) {
                        throw new FailedDependencyException();
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
