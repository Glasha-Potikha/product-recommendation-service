package repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import starBank.example.recomendationService.RecommendationServiceApplication;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = RecommendationServiceApplication.class)
@ActiveProfiles("evaluate")
public class DatabaseConnectionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testSelectFromUsers() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        System.out.println("User count: " + count);
        assertNotNull(count);
    }
}