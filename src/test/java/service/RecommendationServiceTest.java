package service;

import dto.RecommendationDto;
import ruleset.RecommendationRuleSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Mock
    private RecommendationRuleSet rule1;

    @Mock
    private RecommendationRuleSet rule2;

    @InjectMocks
    private RecommendationService service;

    @Test
    void testRecommendationReturnedIfPresent() {
        // Устанавливаем мокирован. правила в сервис!!!!
        service = new RecommendationService(Arrays.asList(rule1, rule2));

        UUID id = UUID.randomUUID();
        RecommendationDto dto = new RecommendationDto(id, "Test", "Text");

        when(rule1.getRecommendation(id)).thenReturn(Optional.of(dto));
        when(rule2.getRecommendation(id)).thenReturn(Optional.empty());

        List<RecommendationDto> res = service.getRecommendationsForUser(id);

        assertEquals(1, res.size());
        assertEquals("Test", res.get(0).getName());
        assertEquals(id, res.get(0).getId());
    }

    @Test
    void testEmptyListWhenNoRecommendations() {
        // Устанавливаем мокирован. правила в сервис !!! обратить внимание
        service = new RecommendationService(Arrays.asList(rule1, rule2));

        UUID id = UUID.randomUUID();

        when(rule1.getRecommendation(id)).thenReturn(Optional.empty());
        when(rule2.getRecommendation(id)).thenReturn(Optional.empty());

        List<RecommendationDto> res = service.getRecommendationsForUser(id);

        assertTrue(res.isEmpty());
    }
}