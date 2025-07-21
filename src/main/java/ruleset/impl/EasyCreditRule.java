package ruleset.impl;

import dto.RecommendationDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ruleset.RecommendationRuleSet;

import java.util.Optional;
import java.util.UUID;

@Component
public class EasyCreditRule implements RecommendationRuleSet {
    private final JdbcTemplate jdbcTemplate;

    public EasyCreditRule(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<RecommendationDto> getRecommendation(UUID userId) {
//условие не использования CREDIT
        Integer creditCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_products WHERE user_id = ? AND product_type = 'CREDIT'",
                Integer.class, userId
        );
        if (creditCount != null && creditCount > 0) return Optional.empty();
        //условие Сумма пополнений больше, чем сумма трат по DEBIT
        Double debitTopups = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND product_type = 'DEBIT' AND transaction_type = 'INCOMING'",
                Double.class, userId);
        Double debitExpenses = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND product_type = 'DEBIT' AND transaction_type = 'OUTGOING'",
                Double.class, userId);
        if (debitTopups <= debitExpenses) return Optional.empty();
        //трат больше чем 100000
        if (debitExpenses <= 100000) return Optional.empty();

        return Optional.of(new RecommendationDto(
                UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f"),
                "Простой кредит",
                "Откройте мир выгодных кредитов с нами!\n" +
                        "\n" +
                        "Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.\n" +
                        "\n" +
                        "Почему выбирают нас:\n" +
                        "\n" +
                        "Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.\n" +
                        "\n" +
                        "Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.\n" +
                        "\n" +
                        "Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.\n" +
                        "\n" +
                        "Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!"
        ));
    }
}
