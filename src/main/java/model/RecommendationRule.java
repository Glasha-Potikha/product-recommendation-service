package model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ruleset.condition.Condition;

import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "recommendation_rules")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RecommendationRule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private UUID productId;

    @Column(columnDefinition = "TEXT")
    private String productText;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "rule_id")
    private List<RuleCondition> conditions;

    public RecommendationRule(UUID productId, String productName, String productDescription, List<RuleCondition> conditions) {
        this.productId = productId;
        this.productName = productName;
        this.productText = productDescription;
        this.conditions = conditions;
    }

}
