package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.response.PostResponse;
import com.guisebastiao.lifeshotsapi.entity.Post;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.mapper.PostMapper;
import com.guisebastiao.lifeshotsapi.repository.PostRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.FeedPostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedPostServiceImpl implements FeedPostService {

    private final PostRepository postRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final PostMapper postMapper;

    public FeedPostServiceImpl(PostRepository postRepository, AuthenticatedUserProvider authenticatedUserProvider, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.postMapper = postMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<List<PostResponse>> feed(PaginationParam pagination) {
        Profile profile = authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit());

        Page<Post> resultPage = postRepository.findAllPostsFromFriends(profile, pageable);

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
}
