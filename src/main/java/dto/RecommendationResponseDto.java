package dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

// DTO для ответа API с рекомендациями
public class RecommendationResponseDto {
    private String userId;
    private List<RecommendationDto> recommendations;

    public RecommendationResponseDto(String userId, List<RecommendationDto> recommendations) {
        this.userId = userId;
        this.recommendations = recommendations;
    }
}
