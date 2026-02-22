package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.response.PostResponse;
import com.guisebastiao.lifeshotsapi.entity.Post;
import com.guisebastiao.lifeshotsapi.mapper.PostMapper;
import com.guisebastiao.lifeshotsapi.repository.PostRepository;
import com.guisebastiao.lifeshotsapi.service.TrendingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TrendingServiceImpl implements TrendingService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public TrendingServiceImpl(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public DefaultResponse<List<PostResponse>> trending(PaginationParam pagination) {
        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit());

        Instant limit = Instant.now().minus(24, ChronoUnit.HOURS);

        Page<Post> resultPage = postRepository.findAllTrendingPosts(limit, pageable);

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
