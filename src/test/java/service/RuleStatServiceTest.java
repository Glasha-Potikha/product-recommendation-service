package service;

import test.BaseIntegrationTest;
import model.RecommendationRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import repository.RecommendationRuleRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RuleStatServiceTest extends BaseIntegrationTest {

    @Autowired
    private RuleStatService ruleStatService;

    @Autowired
    private RecommendationRuleRepository ruleRepository;

    @Test
    void shouldIncrementCounter() {
        // Создаем тестовое правило
        RecommendationRule rule = new RecommendationRule();
        rule.setProductName("Test Rule");
        rule.setProductText("Test Description");
        RecommendationRule savedRule = ruleRepository.save(rule);

        // Проверяем начальное состояние
        assertEquals(0L, ruleStatService.getCountByRuleId(savedRule.getId()));

        // Дважды увеличиваем счетчик
        ruleStatService.incrementCounter(savedRule.getId());
        ruleStatService.incrementCounter(savedRule.getId());

        // Проверяем результат
        assertEquals(2L, ruleStatService.getCountByRuleId(savedRule.getId()));
    }
}