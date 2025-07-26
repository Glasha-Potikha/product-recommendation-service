package ruleset.condition;

import lombok.RequiredArgsConstructor;
import repository.TransactionRepository;

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
