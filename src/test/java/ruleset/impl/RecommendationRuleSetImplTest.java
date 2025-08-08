package ruleset.impl;

import dto.RecommendationDto;
import model.RecommendationRule;
import org.junit.jupiter.api.Test;
import repository.RuleRepository;
import ruleset.RecommendationRuleSet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RecommendationRuleSetImplTest {

    // Тест: должен возвращать рекомендацию, если правило совпадает
    @Test
    void shouldReturnRecommendationIfRuleMatches() {
        // Подготовка тестовых данных
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        // Создаем тестовое правило
        RecommendationRule rule = new RecommendationRule();
        rule.setProductId(productId);
        rule.setProductName("Top Saving");
        rule.setProductText("DEBIT_EXISTS");

        // Мокируем зависимости
        RuleRepository ruleRepository = mock(RuleRepository.class);
        RuleEvaluator evaluator = mock(RuleEvaluator.class);

        // Настраиваем поведение моков
        when(ruleRepository.findAll()).thenReturn(List.of(rule));
        when(evaluator.evaluateRule(rule, userId)).thenReturn(true);

        // Создаем тестируемый объект
        RecommendationRuleSet ruleSet = new RecommendationRuleSetImpl(ruleRepository, evaluator);

        // Вызываем тестируемый метод
        Optional<RecommendationDto> result = ruleSet.getRecommendation(userId);

        // Проверяем результаты
        assertTrue(result.isPresent());
        // Исправлено: используем getProductName() вместо getName()
        assertEquals("Top Saving", result.get().getProductName());
    }

    // Тест: должен возвращать пустой результат, если нет подходящих правил
    @Test
    void shouldReturnEmptyIfNoRulesMatch() {
        // Подготовка тестовых данных
        UUID userId = UUID.randomUUID();

        // Создаем тестовое правило (которое не будет совпадать)
        RecommendationRule rule = new RecommendationRule();
        rule.setProductName("Credit");

        // Мокируем зависимости
        RuleRepository ruleRepository = mock(RuleRepository.class);
        RuleEvaluator evaluator = mock(RuleEvaluator.class);

        // Настраиваем поведение моков
        when(ruleRepository.findAll()).thenReturn(List.of(rule));
        when(evaluator.evaluateRule(rule, userId)).thenReturn(false);

        // Создаем тестируемый объект
        RecommendationRuleSet ruleSet = new RecommendationRuleSetImpl(ruleRepository, evaluator);

        // Вызываем тестируемый метод
        Optional<RecommendationDto> result = ruleSet.getRecommendation(userId);

        // Проверяем, что результат пустой
        assertTrue(result.isEmpty());
    }
}
