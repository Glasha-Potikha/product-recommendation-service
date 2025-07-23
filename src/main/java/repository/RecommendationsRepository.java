package repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public class RecommendationsRepository {

    private final JdbcTemplate jdbcTemplate;

    public RecommendationsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Проверить, использует ли пользователь продукт типа
    public boolean hasProductOfType(UUID userId, String productType) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_products WHERE user_id = ? AND product_type = ?",
                Integer.class, userId, productType
        );
        return count != null && count > 0;
    }

    // Получить сумму пополнений по типу продукта
    public BigDecimal getSumOfDeposits(UUID userId, String productType) {
        return jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                        "WHERE user_id = ? AND product_type = ? AND transaction_type = 'INCOMING'",
                BigDecimal.class, userId, productType
        );
    }

    // Получить сумму трат по типу продукта
    public BigDecimal getSumOfWithdrawals(UUID userId, String productType) {
        return jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                        "WHERE user_id = ? AND product_type = ? AND transaction_type = 'OUTGOING'",
                BigDecimal.class, userId, productType
        );
    }}