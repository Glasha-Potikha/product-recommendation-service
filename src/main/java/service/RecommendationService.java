package service;

import dto.RecommendationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.RecommendationRule;
import model.RuleCondition;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.RuleRepository;
import repository.TransactionRepository;
import ruleset.condition.Condition;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final RuleRepository ruleRepository;
    private final TransactionRepository transactionRepository;
    private final RuleStatService ruleStatService;

    // Это основной метод получения рекомендаций для пользователя. А именно, кеширует результаты и учитывает статистику срабатываний правил.

    @Cacheable(value = "userRecommendations", key = "#userId")
    @Transactional(readOnly = true)
    public List<RecommendationDto> getRecommendationsForUser(UUID userId) {
        log.info("Fetching recommendations for user {}", userId);

        return ruleRepository.findAll().stream()
                .filter(rule -> evaluateRule(rule, userId))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Проверяет соответствие пользователя правилу и обновляет статистику

    private boolean evaluateRule(RecommendationRule rule, UUID userId) {
        boolean isMatch = rule.getConditions().stream()
                .map(condition -> condition.toCondition(transactionRepository))
                .allMatch(condition -> condition.evaluate(userId));

        if (isMatch) {
            ruleStatService.incrementCounter(rule.getId());
            log.debug("Rule {} matched for user {}", rule.getId(), userId);
        }
        return isMatch;
    }

    // Конвертирует RuleCondition в исполняемое условие

    private Condition convertToCondition(RuleCondition condition) {
        return condition.toCondition(transactionRepository);
    }

    // Преобразует правило в DTO для ответа

    private RecommendationDto convertToDto(RecommendationRule rule) {
        return new RecommendationDto(
                rule.getProductId(),
                rule.getProductName(),
                rule.getProductText()
        );
    }

    // Форматированный вывод для Telegram-бота

    public String getFormattedRecommendations(UUID userId) {
        List<RecommendationDto> recommendations = getRecommendationsForUser(userId);

        if (recommendations.isEmpty()) {
            return "Для вас пока нет рекомендаций";
        }

        return recommendations.stream()
                .map(dto -> String.format(
                        "🏦 *%s*\n%s\n",
                        dto.getProductName(),
                        dto.getProductText()))
                .collect(Collectors.joining("\n"));
    }
}