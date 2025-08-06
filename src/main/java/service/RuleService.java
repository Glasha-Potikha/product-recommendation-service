package service;

import dto.ConditionRequest;
import dto.CreateRuleRequest;
import dto.RecommendationDto;
import exceptions.RuleNotFoundException;
import lombok.RequiredArgsConstructor;
import model.RecommendationRule;
import model.RuleCondition;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import repository.RuleRepository;
import repository.TransactionRepository;
import ruleset.condition.*;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;

@Service
@RequiredArgsConstructor
public class RuleService {

    private static final Logger logger = LoggerFactory.getLogger(RuleService.class);

    private final RuleRepository ruleRepository;
    private final TransactionRepository transactionRepository;

    // Создать правило
    public RecommendationRule createRule(CreateRuleRequest request) {
        logger.info("Creating new recommendation rule for product: {}");
        logger.debug("Request payload: {}", request);

        List<RuleCondition> ruleConditions = buildRuleConditions(request.getConditions());

        logger.debug("Built {} conditions for rule", ruleConditions.size());

        RecommendationRule rule = new RecommendationRule(
                request.getProductId(),
                request.getProductName(),
                request.getProductDescription(),
                ruleConditions
        );

        RecommendationRule savedRule = ruleRepository.save(rule);

        logger.info("Successfully created rule with ID: {}", savedRule.getId());
        return savedRule;
    }

    private List<RuleCondition> buildRuleConditions(List<ConditionRequest> conditionRequests) {
        logger.debug("Building RuleCondition entities from {} condition requests", conditionRequests.size());

        return conditionRequests.stream()
                .map(req -> {
                    RuleCondition condition = new RuleCondition();
                    condition.setQuery(req.getType());
                    condition.setArguments(List.of(
                            req.getProductType(),
                            req.getTransactionType(),
                            req.getThreshold().toString(),
                            req.getComparator()
                    ));
                    condition.setNegate(req.getShouldExist() != null && !req.getShouldExist());

                    logger.debug("Created RuleCondition: type={}, args={}", req.getType(), condition.getArguments());
                    return condition;
                })
                .toList();
    }

    // Проверить, подходит ли пользователь под правило
    public boolean isUserEligible(UUID userId, UUID ruleId) {
        logger.debug("Checking eligibility for user {} against rule {}", userId, ruleId);

        RecommendationRule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> {
                    logger.warn("Rule with ID {} not found", ruleId);
                    return new RuleNotFoundException("Rule not found");
                });

        boolean eligible = rule.getConditions().stream()
                .map(condition -> condition.toCondition(transactionRepository))
                .allMatch(condition -> condition.evaluate(userId));

        logger.info("User {} eligibility for rule {}: {}", userId, ruleId, eligible);
        return eligible;
    }

    // Получить все рекомендации для пользователя
    public List<RecommendationDto> getRecommendationsForUser(UUID userId) {
        logger.info("Fetching recommendations for user {}", userId);

        List<RecommendationDto> recommendations = ruleRepository.findAll().stream()
                .filter(rule -> rule.getConditions().stream()
                        .map(c -> c.toCondition(transactionRepository))
                        .allMatch(c -> c.evaluate(userId)))
                .map(rule -> new RecommendationDto(
                        rule.getProductId(),
                        rule.getProductName(),
                        rule.getProductText()
                ))
                .toList();

        logger.info("Found {} recommendations for user {}", recommendations.size(), userId);
        return recommendations;
    }

    public List<RecommendationRule> getAllRules() {
        logger.debug("Fetching all rules from repository");

        List<RecommendationRule> rules = ruleRepository.findAll();

        logger.info("Loaded {} rules from database", rules.size());
        return rules;
    }

    public boolean ruleExists(UUID ruleId) {
        boolean exists = ruleRepository.existsById(ruleId);
        logger.debug("Rule with ID {} exists: {}", ruleId, exists);
        return exists;
    }

    public void deleteRule(UUID ruleId) {
        logger.info("Attempting to delete rule with ID: {}", ruleId);

        if (!ruleRepository.existsById(ruleId)) {
            logger.warn("Attempt to delete non-existent rule: {}", ruleId);
            throw new RuleNotFoundException("Rule not found");
        }

        ruleRepository.deleteById(ruleId);
        logger.info("Successfully deleted rule with ID: {}", ruleId);
    }

    // Внутренний метод: построение дерева условий
    private List<Condition> buildConditions(List<ConditionRequest> conditionRequests) {
        logger.debug("Building Condition tree from {} condition requests", conditionRequests.size());
        return conditionRequests.stream()
                .map(this::mapToCondition)
                .toList();
    }

    private Condition mapToCondition(ConditionRequest req) {
        logger.debug("Mapping condition request: type={}, productType={}", req.getType(), req.getProductType());

        return switch (req.getType()) {
            case "productExists" -> new ProductExistsCondition(
                    transactionRepository,
                    req.getProductType(),
                    req.getShouldExist()
            );
            case "transactionSum" -> new TransactionSumCondition(
                    transactionRepository,
                    req.getProductType(),
                    req.getTransactionType(),
                    req.getThreshold(),
                    req.getComparator()
            );
            case "and" -> new AndCondition(buildConditions(req.getNestedConditions()));
            case "or" -> new OrCondition(buildConditions(req.getNestedConditions()));
            default -> {
                logger.error("Unknown condition type: {}", req.getType());
                throw new IllegalArgumentException("Unknown condition type: " + req.getType());
            }
        };
    }
}