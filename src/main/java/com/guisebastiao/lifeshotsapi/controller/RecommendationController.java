package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;
import com.guisebastiao.lifeshotsapi.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/friends")
    public ResponseEntity<DefaultResponse<PageResponse<ProfileResponse>>> getFriendRecommendations(@Valid PaginationFilter pagination) {
        DefaultResponse<PageResponse<ProfileResponse>> response = this.recommendationService.findFriendRecommendations(pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
