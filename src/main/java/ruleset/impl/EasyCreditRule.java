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
public class EasyCreditRule implements RecommendationRuleSet {

    private final RecommendationsRepository repository;

    public EasyCreditRule(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RecommendationDto> getRecommendation(UUID userId) {
        // Условие: нет CREDIT
        if (repository.hasProductOfType(userId, "CREDIT")) {
            return Optional.empty();
        }

        // Сумма пополнений по DEBIT > суммы трат
        BigDecimal debitDeposits = repository.getSumOfDeposits(userId, "DEBIT");
        BigDecimal debitWithdrawals = repository.getSumOfWithdrawals(userId, "DEBIT");

        if (debitDeposits.compareTo(debitWithdrawals) <= 0) {
            return Optional.empty();
        }

        // Сумма трат > 100 000
        if (debitWithdrawals.compareTo(BigDecimal.valueOf(100_000)) <= 0) {
            return Optional.empty();
        }

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
