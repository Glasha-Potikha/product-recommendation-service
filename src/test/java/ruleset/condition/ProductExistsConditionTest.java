package ruleset.condition;

import repository.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProductExistsConditionTest {
    @Test
    void whenExists_shouldReturnTrue() {
        TransactionRepository repo = mock(TransactionRepository.class);
        UUID id = UUID.randomUUID();
        when(repo.userHasProductType(id, "CREDIT")).thenReturn(true);

        ProductExistsCondition cond = new ProductExistsCondition(repo, "CREDIT", true);
        assertTrue(cond.evaluate(id));
    }
}
