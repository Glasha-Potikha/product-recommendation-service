package repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public class RecommendationsRepository implements TransactionRepository {
    private final JdbcTemplate jdbcTemplate;

    public RecommendationsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Cacheable(value = "userProductsCache", key = "{#userId, #productType}")
    public boolean userHasProductType(UUID userId, String productType) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_products WHERE user_id = ? AND product_type = ?",
                Integer.class,
                userId.toString(),
                productType
        );
        return count != null && count > 0;
    }

    @Override
    @Cacheable(value = "transactionSumsCache", key = "{#userId, #productType, 'DEPOSIT'}")
    public BigDecimal getSumOfDeposits(UUID userId, String productType) {
        return jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                        "WHERE user_id = ? AND product_type = ? AND transaction_type = 'INCOMING'",
                BigDecimal.class,
                userId.toString(),
                productType
        );
    }

    @Override
    @Cacheable(value = "transactionSumsCache", key = "{#userId, #productType, 'WITHDRAWAL'}")
    public BigDecimal getSumOfWithdrawals(UUID userId, String productType) {
        return jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                        "WHERE user_id = ? AND product_type = ? AND transaction_type = 'OUTGOING'",
                BigDecimal.class,
                userId.toString(),
                productType
        );
    }

    @Override
    @Cacheable(value = "transactionSumsCache", key = "{#userId, #productType, #transactionType}")
    public BigDecimal sumByUserIdAndType(UUID userId, String productType, String transactionType) {
        return jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                        "WHERE user_id = ? AND product_type = ? AND transaction_type = ?",
                BigDecimal.class,
                userId.toString(),
                productType,
                transactionType
        );
    }
}