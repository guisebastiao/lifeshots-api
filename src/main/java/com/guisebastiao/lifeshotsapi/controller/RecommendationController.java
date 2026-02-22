package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.RecommendationControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;
import com.guisebastiao.lifeshotsapi.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController implements RecommendationControllerDocs {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/friends")
    public ResponseEntity<DefaultResponse<List<ProfileResponse>>> getFriendRecommendations(@Valid PaginationParam pagination) {
        DefaultResponse<List<ProfileResponse>> response = this.recommendationService.findFriendRecommendations(pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
