package ruleset.condition;

import java.util.List;
import java.util.UUID;

public class AndCondition implements Condition {
    private final List<Condition> conditions;

    public AndCondition(List<Condition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean evaluate(UUID userId) {
        return conditions.stream().allMatch(c -> c.evaluate(userId));
    }

}
