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
public class Invest500Rule implements RecommendationRuleSet {

    private final RecommendationsRepository repository;

    public Invest500Rule(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> getRecommendation(UUID userId) {
        // Есть DEBIT
        if (!repository.hasProductOfType(userId, "DEBIT")) {
            return Optional.empty();
        }

        // Нет INVEST
        if (repository.hasProductOfType(userId, "INVEST")) {
            return Optional.empty();
        }

        // Пополнения по SAVING > 1000
        BigDecimal savingDeposits = repository.getSumOfDeposits(userId, "SAVING");
        if (savingDeposits.compareTo(BigDecimal.valueOf(1000)) <= 0) {
            return Optional.empty();
        }

        return Optional.of(new RecommendationDto(
                UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
                "Invest 500",
                "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!"
        ));
    }
}



