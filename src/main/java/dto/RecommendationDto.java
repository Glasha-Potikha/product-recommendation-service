package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RecommendationDto {
    private final UUID productId;
    private final String productName;
    private final String productText;
}


