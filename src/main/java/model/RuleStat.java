package model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "rule_stats")
@Data
@NoArgsConstructor
public class RuleStat {
    @Id
    @Column(name = "rule_id")
    private UUID ruleId;

    @Column(nullable = false)
    private Long count = 0L;

    public RuleStat(UUID ruleId) {
        this.ruleId = ruleId;
        this.count = 0L;
    }
}