package repository;

import model.RecommendationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RuleRepository extends JpaRepository<RecommendationRule, UUID> {

    List<RecommendationRule> findByProductId(UUID productId);

    boolean existsByProductId(UUID productId);

    boolean existsByProductName(String productName);

    @Query("SELECT r FROM RecommendationRule r JOIN r.conditions c WHERE c.query = :queryType")
    List<RecommendationRule> findByConditionQueryType(@Param("queryType") String queryType);

    void deleteByProductId(UUID productId);
}
