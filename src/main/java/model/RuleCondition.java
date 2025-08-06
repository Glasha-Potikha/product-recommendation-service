package model;

import jakarta.persistence.*;
import lombok.Data;
import repository.TransactionRepository;
import ruleset.condition.Condition;
import ruleset.condition.ProductExistsCondition;
import ruleset.condition.TransactionSumCondition;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
public class RuleCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String query;

    @ElementCollection
    @CollectionTable(name = "rule_arguments", joinColumns = @JoinColumn(name = "condition_id"))
    @Column(name = "argument")
    private List<String> arguments;

    @Column(nullable = false)
    private boolean negate;

    public Condition toCondition(TransactionRepository transactionRepository) {
        return switch (this.query) {
            case "productExists" -> new ProductExistsCondition(
                    transactionRepository,
                    arguments.get(0),
                    !negate
            );
            case "transactionSum" -> new TransactionSumCondition(
                    transactionRepository,
                    arguments.get(0), // productType
                    arguments.get(1), // transactionType
                    new BigDecimal(arguments.get(2)), // threshold
                    arguments.get(3) // comparator
            );
            default -> throw new IllegalArgumentException("Unknown query: " + query);
        };
    }
}
