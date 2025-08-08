package service;

import dto.RecommendationDto;
import model.RecommendationRule;
import model.RuleCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.RuleRepository;
import repository.TransactionRepository;
import ruleset.condition.Condition;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RuleRepository ruleRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RuleStatService ruleStatService;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void testRecommendationReturnedIfPresent() {

        // 1. Подготовка тестовых данных
        UUID userId = UUID.randomUUID();
        UUID ruleId = UUID.randomUUID();

        RecommendationRule rule = new RecommendationRule();
        rule.setId(ruleId);
        rule.setProductName("Test Product");
        rule.setProductText("Recommended for you");

        RuleCondition condition = new RuleCondition();
        // Настройка condition по необходимости

        // 2. Мокирование поведения
        when(ruleRepository.findAll()).thenReturn(Arrays.asList(rule));
        when(condition.toCondition(transactionRepository)).thenReturn(mock(Condition.class));
        when(condition.toCondition(transactionRepository).evaluate(userId)).thenReturn(true);

        // 3. Вызов тестируемого метода
        List<RecommendationDto> result = recommendationService.getRecommendationsForUser(userId);

        // 4. Проверки
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getProductName());
        assertEquals(ruleId, result.get(0).getProductId());

        // Проверяем, что статистика обновляется
        verify(ruleStatService).incrementCounter(ruleId);
    }

    @Test
    void testEmptyListWhenNoRecommendations() {
        // 1. Подготовка
        UUID userId = UUID.randomUUID();

        // 2. Мокирование - нет подходящих правил
        when(ruleRepository.findAll()).thenReturn(Arrays.asList());

        // 3. Вызов
        List<RecommendationDto> result = recommendationService.getRecommendationsForUser(userId);

        // 4. Проверка
        assertTrue(result.isEmpty());
    }
}