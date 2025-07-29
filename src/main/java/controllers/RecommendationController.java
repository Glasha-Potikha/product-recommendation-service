package controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import service.RecommendationService;
import dto.RecommendationDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping("/{userId}")
    public RecommendationResponse getRecommendations(@PathVariable UUID userId) {
        List<RecommendationDto> recs = recommendationService.getRecommendationsForUser(userId);
        return new RecommendationResponse(userId, recs);
    }

    public record RecommendationResponse(UUID userId, List<RecommendationDto> recommendations) {}
}
