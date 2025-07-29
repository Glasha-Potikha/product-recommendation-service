package service;

import dto.RecommendationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ruleset.RecommendationRuleSet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final List<RecommendationRuleSet> rules;

    public List<RecommendationDto> getRecommendationsForUser(UUID userId) {
        return rules.stream()
                .map(r -> r.getRecommendation(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}

