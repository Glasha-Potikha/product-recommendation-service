package ruleset.impl;

import dto.RecommendationDto;
import lombok.RequiredArgsConstructor;
import model.RecommendationRule;
import org.springframework.stereotype.Component;
import repository.RuleRepository;
import ruleset.RecommendationRuleSet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RecommendationRuleSetImpl implements RecommendationRuleSet {
    private final RuleRepository ruleRepository;
    private final RuleEvaluator ruleEvaluator;

    @Override
    public Optional<RecommendationDto> getRecommendation(UUID userId) {
        List<RecommendationRule> rules = ruleRepository.findAll();

        for (RecommendationRule rule : rules) {
            if (ruleEvaluator.evaluateRule(rule, userId)) {
                return Optional.of(new RecommendationDto(
                        rule.getProductId(),
                        rule.getProductName(),
                        rule.getProductText()
                ));
            }
        }
        return Optional.empty();
    }

}
