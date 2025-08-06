package starBank.example.recomendationService;

import dto.CreateRuleRequest;
import dto.ConditionRequest;
import dto.RecommendationDto;
import model.RecommendationRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class RuleIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("starbank")
            .withUsername("postgres")
            .withPassword("password");

    @Autowired
    private TestRestTemplate restTemplate;

    private final String BASE_URL = "/api/rules";

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private UUID testRuleId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // Очистка БД (опционально)
    }

    @Test
    void shouldCreateAndRetrieveRule() {
        // Создаём правило
        CreateRuleRequest request = new CreateRuleRequest();
        request.setProductId(testRuleId);
        request.setProductName("Простой кредит");
        request.setProductDescription("Откройте мир выгодных кредитов...");

        ConditionRequest condition = new ConditionRequest();
        condition.setType("productExists");
        condition.setProductType("CREDIT");
        condition.setShouldExist(false);

        request.setConditions(List.of(condition));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateRuleRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<RecommendationRule> createResponse = restTemplate.postForEntity(BASE_URL, entity, RecommendationRule.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody().getProductId()).isEqualTo(testRuleId);

        // Проверяем, что правило можно получить
        ResponseEntity<List> getAllResponse = restTemplate.getForEntity(BASE_URL, List.class);
        assertThat(getAllResponse.getBody()).isNotEmpty();
    }

    @Test
    void shouldGetRecommendationsForUser() {
        // Предположим, что в БД уже есть правило и транзакции
        UUID userId = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");

        ResponseEntity<List<RecommendationDto>> response = restTemplate.exchange(
                BASE_URL + "/recommendations/" + userId,
                HttpMethod.GET,
                null, // тело запроса не нужно
                new ParameterizedTypeReference<List<RecommendationDto>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void shouldDeleteRule() {
        // Сначала создадим правило
        CreateRuleRequest request = createTestRequest();

        HttpEntity<CreateRuleRequest> entity = new HttpEntity<>(request);
        ResponseEntity<RecommendationRule> createResponse = restTemplate.postForEntity(BASE_URL, entity, RecommendationRule.class);
        UUID ruleId = createResponse.getBody().getId();

        // Удаляем
        restTemplate.delete(BASE_URL + "/" + ruleId);

        // Проверяем, что правило удалено
        ResponseEntity<RecommendationRule> getResponse = restTemplate.getForEntity(BASE_URL + "/" + ruleId, RecommendationRule.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private CreateRuleRequest createTestRequest() {
        ConditionRequest condition = new ConditionRequest();
        condition.setType("productExists");
        condition.setProductType("CREDIT");
        condition.setShouldExist(false);
        condition.setTransactionType(null);
        condition.setThreshold(null);
        condition.setComparator(null);

        CreateRuleRequest request = new CreateRuleRequest();
        request.setProductName("Простой кредит");
        request.setProductDescription("Откройте мир выгодных кредитов...");
        request.setConditions(List.of(condition));

        return request;
    }
}