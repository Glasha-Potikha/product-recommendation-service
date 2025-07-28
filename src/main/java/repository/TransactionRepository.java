package repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionRepository {
    boolean userHasProductType(UUID userId, String productType);
    BigDecimal getSumOfDeposits(UUID userId, String productType);
    BigDecimal getSumOfWithdrawals(UUID userId, String productType);

    BigDecimal sumByUserIdAndType(UUID userId, String productType, String transactionType);
}
