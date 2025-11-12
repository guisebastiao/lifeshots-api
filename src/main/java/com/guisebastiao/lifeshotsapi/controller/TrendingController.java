package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.response.PostResponse;
import com.guisebastiao.lifeshotsapi.service.TrendingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trending")
public class TrendingController {

    @Autowired
    private TrendingService trendingService;

    @GetMapping
    public ResponseEntity<DefaultResponse<PageResponse<PostResponse>>> trending(@Valid PaginationFilter pagination) {
        DefaultResponse<PageResponse<PostResponse>> response = this.trendingService.trending(pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
