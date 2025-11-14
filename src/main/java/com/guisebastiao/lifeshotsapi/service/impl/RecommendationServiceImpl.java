package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.Paging;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.mapper.ProfileMapper;
import com.guisebastiao.lifeshotsapi.repository.FollowRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private ProfileMapper profileMapper;

    @Override
    public DefaultResponse<PageResponse<ProfileResponse>> findFriendRecommendations(PaginationFilter pagination) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit());

        Page<Profile> resultPage = this.followRepository.findFriendRecommendations(profile.getId(), pageable);

        Paging paging = new Paging(resultPage.getTotalElements(), resultPage.getTotalPages(), pagination.offset(), pagination.limit());

        List<ProfileResponse> dataResponse = resultPage.getContent().stream()
                .map(this.profileMapper::toDTO)
                .toList();

        PageResponse<ProfileResponse> data = new PageResponse<ProfileResponse>(dataResponse, paging);

        return new DefaultResponse<PageResponse<ProfileResponse>>(true, "Recomendações de amigos retornados com sucesso", data);
    }
}
