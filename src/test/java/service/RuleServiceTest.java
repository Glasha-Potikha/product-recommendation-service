package service;

import dto.ConditionRequest;
import dto.CreateRuleRequest;
import dto.RecommendationDto;
import exceptions.RuleNotFoundException;
import model.RecommendationRule;
import model.RuleCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.RuleRepository;
import repository.TransactionRepository;
import ruleset.condition.Condition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RuleServiceTest {

    @Mock
    private RuleRepository ruleRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private RuleService ruleService;

    private final UUID TEST_RULE_ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    private final UUID TEST_USER_ID = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");

    private CreateRuleRequest createTestRequest() {
        ConditionRequest condition = new ConditionRequest();
        condition.setType("productExists");
        condition.setProductType("CREDIT");
        condition.setShouldExist(false);
        condition.setTransactionType(null);
        condition.setThreshold(null);
        condition.setComparator(null);

        CreateRuleRequest request = new CreateRuleRequest();
        request.setProductId(TEST_RULE_ID);
        request.setProductName("Простой кредит");
        request.setProductDescription("Откройте мир выгодных кредитов...");
        request.setConditions(List.of(condition));

        return request;
    }

    private RuleCondition createTestRuleCondition() {
        RuleCondition condition = new RuleCondition();
        condition.setQuery("productExists");
        condition.setArguments(List.of("CREDIT", null, null, null));
        condition.setNegate(true);
        return condition;
    }

    private RecommendationRule createTestRule() {
        RecommendationRule rule = new RecommendationRule();
        rule.setId(TEST_RULE_ID);
        rule.setProductId(TEST_RULE_ID);
        rule.setProductName("Простой кредит");
        rule.setProductText("Откройте мир выгодных кредитов...");
        rule.setConditions(List.of(createTestRuleCondition()));
        return rule;
    }

    @Test
    void shouldCreateRule() {
        CreateRuleRequest request = createTestRequest();
        RecommendationRule savedRule = createTestRule();

        when(ruleRepository.save(any(RecommendationRule.class))).thenReturn(savedRule);

        RecommendationRule result = ruleService.createRule(request);

        assertNotNull(result);
        assertEquals(TEST_RULE_ID, result.getProductId());
        assertEquals("Простой кредит", result.getProductName());
        verify(ruleRepository, times(1)).save(any(RecommendationRule.class));
    }

    @Test
    void shouldThrowExceptionWhenRuleNotFound() {
        when(ruleRepository.findById(TEST_RULE_ID)).thenReturn(Optional.empty());

        assertThrows(RuleNotFoundException.class, () ->
                ruleService.isUserEligible(TEST_USER_ID, TEST_RULE_ID)
        );
    }

    @Test
    void shouldCheckUserEligibility() {
        RecommendationRule rule = createTestRule();
        Condition condition = mock(Condition.class);

        when(ruleRepository.findById(TEST_RULE_ID)).thenReturn(Optional.of(rule));
        when(condition.evaluate(TEST_USER_ID)).thenReturn(true);

        // Мокаем toCondition()
        try (MockedStatic<RuleCondition> mocked = mockStatic(RuleCondition.class)) {
            mocked.when(() -> rule.getConditions().get(0).toCondition(transactionRepository))
                    .thenReturn(condition);

            boolean eligible = ruleService.isUserEligible(TEST_USER_ID, TEST_RULE_ID);

            assertTrue(eligible);
            verify(condition, times(1)).evaluate(TEST_USER_ID);
        }
    }

    @Test
    void shouldGetRecommendationsForUser() {
        RecommendationRule rule = createTestRule();
        Condition condition = mock(Condition.class);

        when(ruleRepository.findAll()).thenReturn(List.of(rule));
        when(condition.evaluate(TEST_USER_ID)).thenReturn(true);

        try (MockedStatic<RuleCondition> mocked = mockStatic(RuleCondition.class)) {
            mocked.when(() -> rule.getConditions().get(0).toCondition(transactionRepository))
                    .thenReturn(condition);

            List<RecommendationDto> recommendations = ruleService.getRecommendationsForUser(TEST_USER_ID);

            assertNotNull(recommendations);
            assertEquals(1, recommendations.size());
            assertEquals("Простой кредит", recommendations.get(0));
        }
    }

    @Test
    void shouldGetAllRules() {
        List<RecommendationRule> rules = List.of(createTestRule());
        when(ruleRepository.findAll()).thenReturn(rules);

        List<RecommendationRule> result = ruleService.getAllRules();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TEST_RULE_ID, result.get(0).getProductId());
    }

    @Test
    void shouldDeleteRule_Success() {
        when(ruleRepository.existsById(TEST_RULE_ID)).thenReturn(true);
        doNothing().when(ruleRepository).deleteById(TEST_RULE_ID);

        assertDoesNotThrow(() -> ruleService.deleteRule(TEST_RULE_ID));
        verify(ruleRepository, times(1)).deleteById(TEST_RULE_ID);
    }

    @Test
    void shouldDeleteRule_ThrowExceptionWhenNotFound() {
        when(ruleRepository.existsById(TEST_RULE_ID)).thenReturn(false);

        assertThrows(RuleNotFoundException.class, () -> ruleService.deleteRule(TEST_RULE_ID));
        verify(ruleRepository, never()).deleteById(any());
    }
}