package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.ProfileRequest;
import com.guisebastiao.lifeshotsapi.dto.request.SearchProfileRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.enums.BusinessHttpStatus;
import com.guisebastiao.lifeshotsapi.exception.BusinessException;
import com.guisebastiao.lifeshotsapi.mapper.ProfileMapper;
import com.guisebastiao.lifeshotsapi.repository.ProfileRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.ProfileService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ProfileMapper profileMapper;
    private final MessageSource messageSource;
    private final UUIDConverter uuidConverter;

    public ProfileServiceImpl(ProfileRepository profileRepository, AuthenticatedUserProvider authenticatedUserProvider, ProfileMapper profileMapper, MessageSource messageSource, UUIDConverter uuidConverter) {
        this.profileRepository = profileRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.profileMapper = profileMapper;
        this.messageSource = messageSource;
        this.uuidConverter = uuidConverter;
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
    public DefaultResponse<ProfileResponse> findProfileById(String profileId) {
        Profile profileAuth = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Profile profile = profileRepository.findById(uuidConverter.toUUID(profileId)).
                orElseThrow(() -> new BusinessException(BusinessHttpStatus.NOT_FOUND, getMessage("services.profile-service.methods.find-profile-by-id.not-found")));

        boolean mutualFollow = profileRepository.profilesFollowEachOther(profile, profileAuth);

        if (profile.isPrivate() && !mutualFollow && !profileAuth.getId().equals(profile.getId())) {
            throw new BusinessException(BusinessHttpStatus.PRIVATE_PROFILE, getMessage("services.profile-service.methods.find-profile-by-id.forbidden"));
        }

        return DefaultResponse.success(profileMapper.toDTO(profile));
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

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }
}
