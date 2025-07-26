package ruleset.condition;

import lombok.RequiredArgsConstructor;
import repository.TransactionRepository;

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
        BigDecimal sum;
        if (transactionType.equalsIgnoreCase("INCOMING")) {
            sum = repository.getSumOfDeposits(userId, productType);
        } else if (transactionType.equalsIgnoreCase("OUTGOING")) {
            sum = repository.getSumOfWithdrawals(userId, productType);
        } else {
            throw new IllegalArgumentException("Unknown transaction type: " + transactionType);
        }
        int cmp = sum.compareTo(threshold);
        return switch (comparator) {
            case ">" -> cmp > 0;
            case ">=" -> cmp >= 0;
            case "<" -> cmp < 0;
            case "<=" -> cmp <= 0;
            default -> false;
        };
    }
}
