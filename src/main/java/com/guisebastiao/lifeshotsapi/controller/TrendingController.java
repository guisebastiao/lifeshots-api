package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.TrendingControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.response.PostResponse;
import com.guisebastiao.lifeshotsapi.service.TrendingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trending")
public class TrendingController implements TrendingControllerDocs {

    private final TrendingService trendingService;

    public TrendingController(TrendingService trendingService) {
        this.trendingService = trendingService;
    }

    @GetMapping
    public ResponseEntity<DefaultResponse<List<PostResponse>>> trending(@Valid PaginationParam pagination) {
        DefaultResponse<List<PostResponse>> response = this.trendingService.trending(pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
