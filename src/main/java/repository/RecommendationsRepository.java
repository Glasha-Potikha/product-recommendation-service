package repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public class RecommendationsRepository implements TransactionRepository {

    private final JdbcTemplate jdbcTemplate;
    public RecommendationsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean userHasProductType(UUID userId, String productType) {
        Integer c = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_products WHERE user_id = ? AND product_type = ?",
                Integer.class, userId, productType);
        return c != null && c > 0;
    }

    @Override
    public BigDecimal getSumOfDeposits(UUID userId, String productType) {
        return jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND product_type = ? AND transaction_type = 'INCOMING'",
                BigDecimal.class, userId, productType);
    }

    @Override
    public BigDecimal getSumOfWithdrawals(UUID userId, String productType) {
        return jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND product_type = ? AND transaction_type = 'OUTGOING'",
                BigDecimal.class, userId, productType);
    }

    @Override
    public BigDecimal sumByUserIdAndType(UUID userId, String productType, String transactionType) {
        return jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                        "WHERE user_id = ? AND product_type = ? AND transaction_type = ?",
                BigDecimal.class, userId, productType, transactionType
        );
    }
}