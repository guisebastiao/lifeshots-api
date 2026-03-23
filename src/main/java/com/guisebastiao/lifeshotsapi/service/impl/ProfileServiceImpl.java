package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.ProfileRequest;
import com.guisebastiao.lifeshotsapi.dto.request.SearchProfileRequest;
import com.guisebastiao.lifeshotsapi.dto.response.PostResponse;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;
import com.guisebastiao.lifeshotsapi.entity.Post;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.exception.NotFoundException;
import com.guisebastiao.lifeshotsapi.exception.PrivateProfileException;
import com.guisebastiao.lifeshotsapi.mapper.PostMapper;
import com.guisebastiao.lifeshotsapi.mapper.ProfileMapper;
import com.guisebastiao.lifeshotsapi.repository.PostRepository;
import com.guisebastiao.lifeshotsapi.repository.ProfileRepository;
import com.guisebastiao.lifeshotsapi.security.provider.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.ProfileService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final PostRepository postRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ProfileMapper profileMapper;
    private final UUIDConverter uuidConverter;
    private final PostMapper postMapper;

    public ProfileServiceImpl(ProfileRepository profileRepository, PostRepository postRepository, AuthenticatedUserProvider authenticatedUserProvider, ProfileMapper profileMapper, UUIDConverter uuidConverter, PostMapper postMapper) {
        this.profileRepository = profileRepository;
        this.postRepository = postRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.profileMapper = profileMapper;
        this.uuidConverter = uuidConverter;
        this.postMapper = postMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<ProfileResponse> me() {
        User user = authenticatedUserProvider.getAuthenticatedUser();
        return DefaultResponse.success(profileMapper.toDTO(user.getProfile()));
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<List<ProfileResponse>> searchProfile(SearchProfileRequest dto, PaginationParam pagination) {
        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit());

        Page<Profile> resultPage = profileRepository.searchProfiles(dto.search(), pageable);

        DefaultResponse.Meta meta = DefaultResponse.Meta.builder()
                .totalItems(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .currentPage(pagination.offset())
                .itemsPerPage(pagination.limit())
                .build();

        List<ProfileResponse> data = resultPage.getContent().stream()
                .map(profileMapper::toDTO)
                .toList();

        return DefaultResponse.success(data, meta);
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<ProfileResponse> findProfileByHandle(String handle) {
        Profile profileAuth = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Profile profile = profileRepository.findByHandle(handle)
                .orElseThrow(() -> new NotFoundException("services.profile-service.methods.find-profile-by-handle.not-found"));

        boolean mutualFollow = profileRepository.profilesFollowEachOther(profile, profileAuth);

        if (profile.isPrivate() && !mutualFollow && !profileAuth.getId().equals(profile.getId())) {
            throw new PrivateProfileException();
        }

        return DefaultResponse.success(profileMapper.toDTO(profile));
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<ProfileResponse> findProfileById(String profileId) {
        Profile profileAuth = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Profile profile = profileRepository.findById(uuidConverter.toUUID(profileId)).
                orElseThrow(() -> new NotFoundException("services.profile-service.methods.find-profile-by-id.not-found"));

        boolean mutualFollow = profileRepository.profilesFollowEachOther(profile, profileAuth);

        if (profile.isPrivate() && !mutualFollow && !profileAuth.getId().equals(profile.getId())) {
            throw new PrivateProfileException();
        }

        return DefaultResponse.success(profileMapper.toDTO(profile));
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<List<PostResponse>> findPosts(String profileId, PaginationParam pagination) {
        Profile profileAuth = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Profile profile = profileRepository.findById(uuidConverter.toUUID(profileId))
                .orElseThrow(() -> new NotFoundException("services.profile-service.methods.find-posts.not-found"));

        boolean mutualFollow = profileRepository.profilesFollowEachOther(profile, profileAuth);

        if (profile.isPrivate() && !mutualFollow && !profileAuth.getId().equals(profile.getId())) {
            throw new PrivateProfileException();
        }

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit());

        Page<Post> resultPage = postRepository.findAllByProfile(profile, pageable);

        DefaultResponse.Meta meta = DefaultResponse.Meta.builder()
                .totalItems(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .currentPage(pagination.offset())
                .itemsPerPage(pagination.limit())
                .build();

        List<PostResponse> data = resultPage.getContent().stream()
                .map(postMapper::toDTO)
                .toList();

        return DefaultResponse.success(data, meta);
    }

    @Override
    @Transactional
    public DefaultResponse<ProfileResponse> updateProfile(ProfileRequest dto) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        Profile profile = user.getProfile();

        profileMapper.updateProfile(dto, profile);

        profileRepository.save(profile);

        return DefaultResponse.success(profileMapper.toDTO(profile));
    }
}
