package controllers;


import dto.RecommendationDto;
import dto.RecommendationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.RecommendationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/{user_id}")
    public ResponseEntity<RecommendationResponse> getRecommendations(@PathVariable("user_id") UUID userId) {
        List<RecommendationDto> recommendations = recommendationService.getRecommendationsForUser(userId);
        return ResponseEntity.ok(new RecommendationResponse(userId, recommendations));
    }

    record RecommendationResponse(UUID user_id, List<RecommendationDto> recommendations) {}
}

