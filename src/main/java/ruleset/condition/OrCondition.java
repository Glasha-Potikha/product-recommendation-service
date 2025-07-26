package ruleset.condition;

import java.util.List;
import java.util.UUID;

public class OrCondition implements Condition{
    private final List<Condition> conditions;

    public OrCondition(List<Condition> conditions){
        this.conditions = conditions;
    }

    @Override
    public boolean evaluate(UUID userId){
        return conditions.stream().anyMatch(c -> c.evaluate(userId));
    }
}
