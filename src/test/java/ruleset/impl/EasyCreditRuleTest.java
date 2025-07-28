package ruleset.impl;

import dto.RecommendationDto;
import repository.RecommendationsRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EasyCreditRuleTest {

    // Тест когда пользователь не подходит!

    @Test
    void whenUserHasCredit_noRecommendation() {
        RecommendationsRepository repo = mock(RecommendationsRepository.class);
        UUID id = UUID.randomUUID();
        when(repo.userHasProductType(id, "CREDIT")).thenReturn(true); // кредит уже есть

        EasyCreditRule rule = new EasyCreditRule(repo);
        assertTrue(rule.getRecommendation(id).isEmpty());
    }

    // Тест когда пользователь подходит!

    @Test
    void whenEligible_returnsRecommendation() {
        RecommendationsRepository repo = mock(RecommendationsRepository.class);
        UUID id = UUID.randomUUID();
        when(repo.userHasProductType(id, "CREDIT")).thenReturn(false);
        when(repo.getSumOfDeposits(id, "DEBIT")).thenReturn(new BigDecimal("200000"));
        when(repo.getSumOfWithdrawals(id, "DEBIT")).thenReturn(new BigDecimal("150000"));

        EasyCreditRule rule = new EasyCreditRule(repo);
        Optional<RecommendationDto> opt = rule.getRecommendation(id);

        assertTrue(opt.isPresent());
        assertEquals("Простой кредит", opt.get().getName());
    }
}
