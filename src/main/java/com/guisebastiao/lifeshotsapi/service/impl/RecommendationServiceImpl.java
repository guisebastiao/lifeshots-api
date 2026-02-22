package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.mapper.ProfileMapper;
import com.guisebastiao.lifeshotsapi.repository.FollowRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.RecommendationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final FollowRepository followRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ProfileMapper profileMapper;

    public RecommendationServiceImpl(FollowRepository followRepository, AuthenticatedUserProvider authenticatedUserProvider, ProfileMapper profileMapper) {
        this.followRepository = followRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.profileMapper = profileMapper;
    }


    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<List<ProfileResponse>> findFriendRecommendations(PaginationParam pagination) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit());

        Page<Profile> resultPage = this.followRepository.findFriendRecommendations(profile.getId(), pageable);

        DefaultResponse.Meta meta = DefaultResponse.Meta.builder()
                .totalItems(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .currentPage(pagination.offset())
                .itemsPerPage(pagination.limit())
                .build();

        List<ProfileResponse> data = resultPage.getContent().stream()
                .map(this.profileMapper::toDTO)
                .toList();

        return DefaultResponse.success(data, meta);
    }
}
