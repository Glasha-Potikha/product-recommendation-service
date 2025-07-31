package ruleset.impl;

import dto.RecommendationDto;
import model.RecommendationRule;
import org.junit.jupiter.api.Test;
import repository.RuleRepository;
import ruleset.RecommendationRuleSet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecommendationRuleSetImplTest {
    @Test
    void shouldReturnRecommendationIfRuleMatches() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        RecommendationRule rule = new RecommendationRule();
        rule.setProductId(productId);
        rule.setProductName("Top Saving");
        rule.setProductText("DEBIT_EXISTS");

        RuleRepository ruleRepository = mock(RuleRepository.class);
        RuleEvaluator evaluator = mock(RuleEvaluator.class);

        when(ruleRepository.findAll()).thenReturn(List.of(rule));
        when(evaluator.evaluateRule(rule, userId)).thenReturn(true);

        RecommendationRuleSet ruleSet = new RecommendationRuleSetImpl(ruleRepository, evaluator);
        Optional<RecommendationDto> result = ruleSet.getRecommendation(userId);

        assertTrue(result.isPresent());
        assertEquals("Top Saving", result.get().getName());
    }

    @Test
    void shouldReturnEmptyIfNoRulesMatch() {
        UUID userId = UUID.randomUUID();
        RecommendationRule rule = new RecommendationRule();
        rule.setProductName("Credit");

        RuleRepository ruleRepository = mock(RuleRepository.class);
        RuleEvaluator evaluator = mock(RuleEvaluator.class);

        when(ruleRepository.findAll()).thenReturn(List.of(rule));
        when(evaluator.evaluateRule(rule, userId)).thenReturn(false);

        RecommendationRuleSet ruleSet = new RecommendationRuleSetImpl(ruleRepository, evaluator);
        Optional<RecommendationDto> result = ruleSet.getRecommendation(userId);

        assertTrue(result.isEmpty());
    }
}
