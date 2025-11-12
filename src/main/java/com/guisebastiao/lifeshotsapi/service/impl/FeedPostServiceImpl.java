package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.Paging;
import com.guisebastiao.lifeshotsapi.dto.response.PostResponse;
import com.guisebastiao.lifeshotsapi.entity.Post;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.mapper.PostMapper;
import com.guisebastiao.lifeshotsapi.repository.PostRepository;
import com.guisebastiao.lifeshotsapi.security.AuthenticatedUserProvider;
import com.guisebastiao.lifeshotsapi.service.FeedPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedPostServiceImpl implements FeedPostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private PostMapper postMapper;

    @Override
    public DefaultResponse<PageResponse<PostResponse>> feed(PaginationFilter pagination) {
        Profile profile = this.authenticatedUserProvider.getAuthenticatedUser().getProfile();

        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit());

        Page<Post> resultPage = this.postRepository.findAllPostsFromFriends(profile, pageable);

        Paging paging = new Paging(resultPage.getTotalElements(), resultPage.getTotalPages(), pagination.offset(), pagination.limit());

        List<PostResponse> dataResponse = resultPage.getContent().stream()
                .map(this.postMapper::toDTO)
                .toList();

        PageResponse<PostResponse> data = new PageResponse<PostResponse>(dataResponse, paging);

        return new DefaultResponse<PageResponse<PostResponse>>(true, "Feed de publicações retornado com sucesso", data);
    }
}
