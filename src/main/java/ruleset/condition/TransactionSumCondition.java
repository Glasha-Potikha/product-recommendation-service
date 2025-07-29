package ruleset.condition;

import repository.TransactionRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
public class TransactionSumCondition implements Condition {
    private final TransactionRepository repository;
    private final String productType;
    private final String transactionType;
    private final BigDecimal threshold;
    private final String comparator;

    @Override
    public boolean evaluate(UUID userId) {
        // Получаем сумму транзакций по пользователю, продукту и типу транзакции
        BigDecimal sum = repository.sumByUserIdAndType(userId, productType, transactionType);

        // Если сумма null, считаем её нулём
        if (sum == null) {
            sum = BigDecimal.ZERO;
        }

        // Сравниваем сумму с порогом
        int cmp = sum.compareTo(threshold); // cmp > 0, < 0 или == 0

        return switch (comparator) {
            case ">" -> cmp > 0;
            case ">=" -> cmp >= 0;
            case "<" -> cmp < 0;
            case "<=" -> cmp <= 0;
            case "==" -> cmp == 0;
            case "!=" -> cmp != 0;
            default -> false;
        };
    }
}