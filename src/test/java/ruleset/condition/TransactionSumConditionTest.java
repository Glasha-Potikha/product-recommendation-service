package ruleset.condition;

import repository.TransactionRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ruleset.condition.TransactionSumCondition;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TransactionSumConditionTest {

    static Stream<Arguments> comparisonTestCases() {
        return Stream.of(
                Arguments.of(">", new BigDecimal("2000"), new BigDecimal("1000"), true),
                Arguments.of(">=", new BigDecimal("2000"), new BigDecimal("1000"), true),
                Arguments.of("<", new BigDecimal("2000"), new BigDecimal("1000"), false),
                Arguments.of("<=", new BigDecimal("2000"), new BigDecimal("1000"), false),
                Arguments.of("==", new BigDecimal("1000"), new BigDecimal("1000"), true),
                Arguments.of("!=", new BigDecimal("1000"), new BigDecimal("1000"), false),
                Arguments.of(">", BigDecimal.ZERO, new BigDecimal("1000"), false),
                Arguments.of(">", null, new BigDecimal("1000"), false)
        );
    }

    @ParameterizedTest
    @MethodSource("comparisonTestCases")
    void testComparison(String op, BigDecimal sum, BigDecimal threshold, boolean expected) {
        TransactionRepository repo = mock(TransactionRepository.class);
        UUID id = UUID.randomUUID();

        when(repo.sumByUserIdAndType(id, "SAVING", "INCOMING"))
                .thenReturn(sum);

        TransactionSumCondition cond =
                new TransactionSumCondition(repo, "SAVING", "INCOMING", threshold, op);
        assertEquals(expected, cond.evaluate(id));
    }
}