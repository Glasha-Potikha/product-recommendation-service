package ruleset.condition;
import repository.TransactionRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class ProductExistsCondition implements Condition {
    private final TransactionRepository repository;
    private final String productType;
    private final boolean shouldExist;

    @Override
    public boolean evaluate(UUID userId) {
        boolean exists = repository.userHasProductType(userId, productType);
        return shouldExist == exists;
    }
}
