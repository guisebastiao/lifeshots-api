package com.guisebastiao.lifeshotsapi.service.impl;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.Paging;
import com.guisebastiao.lifeshotsapi.dto.response.PostResponse;
import com.guisebastiao.lifeshotsapi.entity.Post;
import com.guisebastiao.lifeshotsapi.mapper.PostMapper;
import com.guisebastiao.lifeshotsapi.repository.PostRepository;
import com.guisebastiao.lifeshotsapi.service.TrendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TrendingServiceImpl implements TrendingService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;

    @Override
    public DefaultResponse<PageResponse<PostResponse>> trending(PaginationFilter pagination) {
        Pageable pageable = PageRequest.of(pagination.offset() - 1, pagination.limit());

        Instant limit = Instant.now().minus(24, ChronoUnit.HOURS);

        Page<Post> resultPage = this.postRepository.findAllTrendingPosts(limit, pageable);

        Paging paging = new Paging(resultPage.getTotalElements(), resultPage.getTotalPages(), pagination.offset(), pagination.limit());

        List<PostResponse> dataResponse = resultPage.getContent().stream()
                .map(this.postMapper::toDTO)
                .toList();

        PageResponse<PostResponse> data = new PageResponse<PostResponse>(dataResponse, paging);

        return new DefaultResponse<PageResponse<PostResponse>>(true, "Publicações em alta retornadas com sucesso", data);
    }
}
