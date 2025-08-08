package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import model.RuleStat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RuleStatRepository extends JpaRepository<RuleStat, UUID> {

    @Query("SELECT rs FROM RuleStat rs WHERE rs.rule.id = :ruleId")
    Optional<RuleStat> findByRuleId(@Param("ruleId") UUID ruleId);
}
