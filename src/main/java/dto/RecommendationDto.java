package dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class RecommendationDto {
    private UUID id;
    private String name;
    private String text;

    public RecommendationDto() {}
    public RecommendationDto(UUID id, String name, String text) {
        this.id = id;
        this.text = text;
        this.name = name;
    }

}


