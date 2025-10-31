package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.Paging;
import com.guisebastiao.lifeshotsapi.dto.response.FollowResponse;
import com.guisebastiao.lifeshotsapi.entity.Follow;
import com.guisebastiao.lifeshotsapi.entity.FollowId;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.entity.User;
import com.guisebastiao.lifeshotsapi.enums.FollowType;
import com.guisebastiao.lifeshotsapi.mapper.FollowMapper;
import com.guisebastiao.lifeshotsapi.repository.FollowRepository;
import com.guisebastiao.lifeshotsapi.repository.ProfileRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.FollowService;
import com.guisebastiao.lifeshotsapi.util.UUIDConverter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private FollowMapper followMapper;

    @Override
    @Transactional
    public DefaultResponse<Void> follow(String profileId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        Profile following = this.profileRepository.findById(UUIDConverter.toUUID(profileId))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado"));

        if (user.getProfile().getId().equals(following.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você não pode seguir você mesmo");
        }

        if (this.followRepository.alreadyFollowingAccount(following, user.getProfile())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já segue está conta");
        }

        FollowId followId = new FollowId();
        followId.setFollowingId(following.getId());
        followId.setFollowerId(user.getProfile().getId());

        Follow follow = new Follow();
        follow.setId(followId);
        follow.setFollower(user.getProfile());
        follow.setFollowing(following);

        this.followRepository.save(follow);

        following.setFollowersCount(following.getFollowersCount() + 1);
        user.getProfile().setFollowingCount(user.getProfile().getFollowingCount() + 1);

        this.profileRepository.saveAll(List.of(following, user.getProfile()));

        return new DefaultResponse<Void>(true, String.format("Você está seguindo %s", following.getUser().getHandle()), null);
    }

    @Override
    public DefaultResponse<PageResponse<FollowResponse>> findAllMyFollowers(FollowType type, PaginationFilter pagination) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit());

        Page<Follow> resultPage;

        if (type == FollowType.FOLLOWERS) {
            resultPage = this.followRepository.findByFollowing(user.getProfile(), pageable);
        } else {
            resultPage = this.followRepository.findByFollower(user.getProfile(), pageable);
        }

        Paging paging = new Paging(resultPage.getTotalElements(), resultPage.getTotalPages(), pagination.offset(), pagination.limit());

        List<FollowResponse> dataResponse = resultPage.getContent().stream()
                .map(follow -> (type == FollowType.FOLLOWERS)
                        ? this.followMapper.toFollowerDTO(follow)
                        : this.followMapper.toFollowingDTO(follow))
                .toList();

        PageResponse<FollowResponse> data = new PageResponse<FollowResponse>(dataResponse, paging);

        return new DefaultResponse<PageResponse<FollowResponse>>(true, "Seguidores retornados com sucesso", data);
    }

    @Override
    public DefaultResponse<PageResponse<FollowResponse>> findAllFollowers(String profileId, FollowType type, PaginationFilter pagination) {
        Profile profile = this.profileRepository.findById(UUIDConverter.toUUID(profileId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado"));

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit());

        Page<Follow> resultPage;

        if (type == FollowType.FOLLOWERS) {
            resultPage = this.followRepository.findByFollowing(profile, pageable);
        } else {
            resultPage = this.followRepository.findByFollower(profile, pageable);
        }

        Paging paging = new Paging(resultPage.getTotalElements(), resultPage.getTotalPages(), pagination.offset(), pagination.limit());

        List<FollowResponse> dataResponse = resultPage.getContent().stream()
                .map(follow -> (type == FollowType.FOLLOWERS)
                        ? this.followMapper.toFollowerDTO(follow)
                        : this.followMapper.toFollowingDTO(follow))
                .toList();

        PageResponse<FollowResponse> data = new PageResponse<FollowResponse>(dataResponse, paging);

        return new DefaultResponse<PageResponse<FollowResponse>>(true, "Seguidores retornados com sucesso", data);
    }

    @Override
    @Transactional
    public DefaultResponse<Void> unfollow(String profileId) {
        User user = authenticatedUserProvider.getAuthenticatedUser();

        Profile profile = this.profileRepository.findById(UUIDConverter.toUUID(profileId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado"));

        if (user.getProfile().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você não pode seguir você mesmo");
        }

        Follow follow = this.followRepository.findByFollowingAndFollower(profile, user.getProfile())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Você não segue o usuário %s", profile.getUser().getHandle())));

        this.followRepository.delete(follow);

        profile.setFollowersCount(profile.getFollowersCount() + 1);
        user.getProfile().setFollowingCount(user.getProfile().getFollowingCount() + 1);

        this.profileRepository.saveAll(List.of(profile, user.getProfile()));

        return new DefaultResponse<Void>(true, String.format("Você parou de seguir %s", profile.getUser().getHandle()), null);
    }
}
