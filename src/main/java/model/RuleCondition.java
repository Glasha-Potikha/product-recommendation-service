package model;

import jakarta.persistence.*;
import lombok.Data;

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
}
