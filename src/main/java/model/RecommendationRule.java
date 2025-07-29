package model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "recommendation_rules")
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
}
