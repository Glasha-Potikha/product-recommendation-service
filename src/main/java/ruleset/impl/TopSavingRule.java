package ruleset.impl;

import dto.RecommendationDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import repository.RecommendationsRepository;
import ruleset.RecommendationRuleSet;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRule implements RecommendationRuleSet {

    private final RecommendationsRepository repository;

    public TopSavingRule(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> getRecommendation(UUID userId) {
        // Есть DEBIT
        if (!repository.hasProductOfType(userId, "DEBIT")) {
            return Optional.empty();
        }

        // (DEBIT пополнения >= 50k) ИЛИ (SAVING пополнения >= 50k)
        BigDecimal debitDeposits = repository.getSumOfDeposits(userId, "DEBIT");
        BigDecimal savingDeposits = repository.getSumOfDeposits(userId, "SAVING");

        if (debitDeposits.compareTo(BigDecimal.valueOf(50_000)) < 0 &&
                savingDeposits.compareTo(BigDecimal.valueOf(50_000)) < 0) {
            return Optional.empty();
        }

        // Пополнения по DEBIT > трат
        BigDecimal debitWithdrawals = repository.getSumOfWithdrawals(userId, "DEBIT");
        if (debitDeposits.compareTo(debitWithdrawals) <= 0) {
            return Optional.empty();
        }

        return Optional.of(new RecommendationDto(
                UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"),
                "Top Saving",
                "Откройте свою собственную «Копилку» с нашим банком! ..."
        ));
    }
}
