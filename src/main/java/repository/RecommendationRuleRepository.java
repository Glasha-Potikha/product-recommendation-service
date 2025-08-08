package repository;

import model.RecommendationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RecommendationRuleRepository
        extends JpaRepository<RecommendationRule, UUID> {
}