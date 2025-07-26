package service;

import dto.RecommendationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ruleset.RecommendationRuleSet;

import java.math.BigDecimal;
import java.util.*;

    @Service
    @RequiredArgsConstructor
    public class RecommendationService {
        @Autowired
        private final List<RecommendationRuleSet> rules;

        public List<RecommendationDto> getRecommendationsForUser(UUID userId) {
            return rules.stream()
                    .map(rule -> rule.getRecommendation(userId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }

    }

