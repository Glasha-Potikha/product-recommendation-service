package ruleset.impl;

import dto.RecommendationDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ruleset.RecommendationRuleSet;

import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRule implements RecommendationRuleSet {
    private final JdbcTemplate jdbcTemplate;

    public TopSavingRule(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<RecommendationDto> getRecommendation(UUID userId) {
        //условие с использованием DEBIT, хотя бы раз
        Integer debitCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_products WHERE user_id = ? AND product_type = 'DEBIT'",
                Integer.class, userId
        );
        if (debitCount == null || debitCount == 0) return Optional.empty();

//условие с суммой пополнений
        Double debitTopups = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND product_type = 'DEBIT' AND transaction_type = 'INCOMING'",
                Double.class, userId);
        Double savingTopups = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND product_type = 'SAVING' AND transaction_type = 'INCOMING'",
                Double.class, userId);
        if ((debitTopups < 50000) && (savingTopups < 50000)) return Optional.empty();
//условие Сумма пополнений больше, чем сумма трат по DEBIT
        Double debitExpenses = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND product_type = 'DEBIT' AND transaction_type = 'OUTGOING'",
                Double.class, userId);
        if (debitTopups <= debitExpenses) return Optional.empty();
        return Optional.of(new RecommendationDto(
                UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"),
                "Top Saving",
                "Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!\n" +
                        "\n" +
                        "Преимущества «Копилки»:\n" +
                        "\n" +
                        "Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет.\n" +
                        "\n" +
                        "Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.\n" +
                        "\n" +
                        "Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг.\n" +
                        "\n" +
                        "Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!"
        ));
    }
}
