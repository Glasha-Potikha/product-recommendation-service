package ruleset.impl;

import model.RecommendationRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.TransactionRepository;
import ruleset.parser.TextRuleParser;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RuleEvaluatorTest {
    private TransactionRepository transactionRepository;
    private TextRuleParser parser;
    private RuleEvaluator evaluator;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        parser = new TextRuleParser(transactionRepository);
        evaluator = new RuleEvaluator(transactionRepository, parser);
    }

    @Test
    void shouldReturnTrueForMatchingRule() {
        RecommendationRule rule = new RecommendationRule();
        rule.setProductText("DEBIT_EXISTS");
        UUID userId = UUID.randomUUID();

        when(transactionRepository.userHasProductType(userId, "DEBIT")).thenReturn(true);

        boolean result = evaluator.evaluateRule(rule, userId);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseForNonMatchingRule() {
        RecommendationRule rule = new RecommendationRule();
        rule.setProductText("INVEST_NOT_EXISTS");
        UUID userId = UUID.randomUUID();

        when(transactionRepository.userHasProductType(userId, "INVEST")).thenReturn(true);

        boolean result = evaluator.evaluateRule(rule, userId);
        assertFalse(result);
    }
}
