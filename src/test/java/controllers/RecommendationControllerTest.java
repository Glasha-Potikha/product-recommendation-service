package controllers;

import dto.RecommendationDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import service.RecommendationService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecommendationService service;

    @InjectMocks
    private RecommendationController controller;

    @Test
    void testGetRecommendations_returnsOk() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        UUID userId = UUID.randomUUID();
        List<RecommendationDto> list = List.of(new RecommendationDto(userId, "Test", "text"));
        when(service.getRecommendationsForUser(userId)).thenReturn(list);

        mockMvc.perform(get("/recommendation/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.recommendations[0].name").value("Test"));
    }
}