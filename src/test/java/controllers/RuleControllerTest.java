package controllers;

import dto.ConditionRequest;
import dto.CreateRuleRequest;
import dto.RecommendationDto;
import exceptions.RuleNotFoundException;
import model.RecommendationRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import service.RuleService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RuleController.class)
public class RuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RuleService ruleService;

    private final UUID TEST_RULE_ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    private final UUID TEST_USER_ID = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");

    // Пример запроса на создание правила
    private CreateRuleRequest createTestRequest() {
        ConditionRequest condition = new ConditionRequest();
        condition.setType("productExists");
        condition.setProductType("CREDIT");
        condition.setShouldExist(false);

        CreateRuleRequest request = new CreateRuleRequest();
        request.setProductId(TEST_RULE_ID);
        request.setProductName("Простой кредит");
        request.setProductDescription("Откройте мир выгодных кредитов...");
        request.setConditions(List.of(condition));

        return request;
    }

    // Пример правила
    private RecommendationRule createTestRule() {
        RecommendationRule rule = new RecommendationRule();
        rule.setId(TEST_RULE_ID);
        rule.setProductId(TEST_RULE_ID);
        rule.setProductName("Простой кредит");
        rule.setProductText("Откройте мир выгодных кредитов...");
        return rule;
    }

    // Пример рекомендации
    private RecommendationDto createTestDto() {
        return new RecommendationDto(
                TEST_RULE_ID,
                "Простой кредит",
                "Откройте мир выгодных кредитов..."
        );
    }

    @Test
    void shouldCreateRule() throws Exception {
        CreateRuleRequest request = createTestRequest();
        RecommendationRule savedRule = createTestRule();

        when(ruleService.createRule(request)).thenReturn(savedRule);

        mockMvc.perform(post("/api/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "productId": "147f6a0f-3b91-413b-ab99-87f081d60d5a",
                                "productName": "Простой кредит",
                                "productDescription": "Откройте мир выгодных кредитов...",
                                "conditions": [
                                    {
                                        "type": "productExists",
                                        "productType": "CREDIT",
                                        "shouldExist": false
                                    }
                                ]
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(TEST_RULE_ID.toString()))
                .andExpect(jsonPath("$.productName").value("Простой кредит"));

        verify(ruleService, times(1)).createRule(any(CreateRuleRequest.class));
    }

    @Test
    void shouldCheckUserEligibility() throws Exception {
        when(ruleService.isUserEligible(TEST_USER_ID, TEST_RULE_ID)).thenReturn(true);

        mockMvc.perform(get("/api/rules/check/{ruleId}", TEST_RULE_ID)
                        .param("userId", TEST_USER_ID.toString()))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().string("true"));

        verify(ruleService, times(1)).isUserEligible(TEST_USER_ID, TEST_RULE_ID);
    }

    @Test
    void shouldGetRecommendationsForUser() throws Exception {
        List<RecommendationDto> recommendations = List.of(createTestDto());

        when(ruleService.getRecommendationsForUser(TEST_USER_ID)).thenReturn(recommendations);

        mockMvc.perform(get("/api/rules/recommendations/{userId}", TEST_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Простой кредит"));

        verify(ruleService, times(1)).getRecommendationsForUser(TEST_USER_ID);
    }

    @Test
    void shouldGetAllRules() throws Exception {
        List<RecommendationRule> rules = List.of(createTestRule());

        when(ruleService.getAllRules()).thenReturn(rules);

        mockMvc.perform(get("/api/rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].productName").value("Простой кредит"));

        verify(ruleService, times(1)).getAllRules();
    }

    @Test
    void shouldDeleteRule_Success() throws Exception {
        doNothing().when(ruleService).deleteByProductId(TEST_RULE_ID);

        mockMvc.perform(delete("/api/rules/{ruleId}", TEST_RULE_ID))
                .andExpect(status().isOk());

        verify(ruleService, times(1)).deleteByProductId(TEST_RULE_ID);
    }

    @Test
    void shouldDeleteRule_NotFound() throws Exception {
        doThrow(new RuleNotFoundException("Rule not found"))
                .when(ruleService).deleteByProductId(TEST_RULE_ID);

        mockMvc.perform(delete("/api/rules/{ruleId}", TEST_RULE_ID))
                .andExpect(status().isNotFound());

        verify(ruleService, times(1)).deleteByProductId(TEST_RULE_ID);
    }
}