package ruleset.condition;
import java.util.UUID;

//все проверки наследуются от него
public interface Condition {
    boolean evaluate(UUID userId);
}
