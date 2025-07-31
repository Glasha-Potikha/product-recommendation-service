package ruleset.impl;

import lombok.RequiredArgsConstructor;
import model.RecommendationRule;
import org.springframework.stereotype.Component;
import repository.TransactionRepository;
import ruleset.condition.Condition;
import ruleset.parser.TextRuleParser;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RuleEvaluator {
    private final TransactionRepository repository;
    private final TextRuleParser parser;

    public boolean evaluateRule(RecommendationRule rule, UUID userId) {
        Condition condition = parser.parse(rule.getProductText());
        return condition.evaluate(userId);
    }
}
