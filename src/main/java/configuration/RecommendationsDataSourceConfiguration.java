package configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class RecommendationsDataSourceConfiguration {
    @Bean(name = "recommendationsDataSource")
    public DataSource recommendationsDataSource(
            @Value("${application.recommendations-db.url}") String recommendationsUrl,
            @Value("${application.recommendations-db.pool-size:10}") int poolSize) {
        HikariDataSource ds = new HikariDataSource();
        // добавил еще дополнительные параметры для пула соединений
        ds.setJdbcUrl(recommendationsUrl);
        ds.setDriverClassName("org.h2.Driver");
        ds.setReadOnly(true);
        ds.setMaximumPoolSize(poolSize);
        ds.setConnectionTimeout(30000);
        ds.setIdleTimeout(600000);
        ds.setMaxLifetime(1800000);
        return ds;
    }

    @Bean(name = "recommendationsJdbcTemplate")
    public JdbcTemplate recommendationsJdbcTemplate(DataSource recommendationsDataSource) {
        return new JdbcTemplate(recommendationsDataSource);
    }
}