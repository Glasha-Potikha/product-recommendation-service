package service;

import dto.RuleStatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.RuleStat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.RecommendationRuleRepository;
import repository.RuleStatRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RuleStatService {
    private final RuleStatRepository ruleStatRepository;
    private final RecommendationRuleRepository ruleRepository;

    // Увеличивает счетчик срабатываний правила @param ruleId ID правила для обновления статистики

    @Transactional
    public void incrementCounter(UUID ruleId) {
        RuleStat stat = ruleStatRepository.findById(ruleId)
                .orElseGet(() -> {
                    log.debug("Creating new stat counter for rule {}", ruleId);
                    RuleStat newStat = new RuleStat();
                    newStat.setRuleId(ruleId);
                    return ruleStatRepository.save(newStat);
                });

        stat.setCount(stat.getCount() + 1);
        log.trace("Incremented counter for rule {} (new value: {})", ruleId, stat.getCount());
    }

    // Вернем количество срабатываний правила @param ruleId ID правила @return Количество срабатываний (0 если правило не найдено)

    @Transactional(readOnly = true)
    public Long getCountByRuleId(UUID ruleId) {
        return ruleStatRepository.findById(ruleId)
                .map(RuleStat::getCount)
                .orElse(0L);
    }

    // Сбрасывает счетчик для правила @param ruleId ID правила

    @Transactional
    public void resetCounter(UUID ruleId) {
        ruleStatRepository.findById(ruleId).ifPresent(stat -> {
            stat.setCount(0L);
            log.debug("Reset counter for rule {}", ruleId);
        });
    }

    // Получает статистику по всем правилам @return Список статистик с названиями правил

    @Transactional(readOnly = true)
    public List<RuleStatResponse> getAllStats() {
        return ruleRepository.findAll().stream()
                .map(rule -> {
                    Long count = getCountByRuleId(rule.getId());
                    return new RuleStatResponse(
                            rule.getId(),
                            rule.getProductName(),
                            count
                    );
                })
                .collect(Collectors.toList());
    }
}