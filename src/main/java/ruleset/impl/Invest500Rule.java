package ruleset.impl;

import dto.RecommendationDto;
import org.springframework.jdbc.core.JdbcTemplate;
import ruleset.RecommendationRuleSet;

import java.util.Optional;
import java.util.UUID;

public class Invest500Rule implements RecommendationRuleSet {
    private final JdbcTemplate jdbcTemplate;

    public Invest500Rule(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<RecommendationDto> getRecommendation(UUID userId) {
        //1.Есть ли DEBIT
        Integer debitCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_product WHERE user_id = ? AND product_type = 'DEBIT'",
                Integer.class, userId
        );
        if (debitCount == null || debitCount == 0) return Optional.empty();

        //2.Условие с INVEST
        Integer investCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_products WHERE user_id = ? AND product_type = 'INVEST'",
                Integer.class, userId
        );
        if (investCount != null && investCount > 0) return Optional.empty();

        //3. Что SAVING > 1000
        Double savingTopups = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND product_type = 'SAVING' AND transaction_type = 'INCOMING'",
                Double.class, userId);
        if (savingTopups <= 1000) return Optional.empty();

        return Optional.of(new RecommendationDto(
                UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
                "Invest 500",
                "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!"
        ));
    }
}


