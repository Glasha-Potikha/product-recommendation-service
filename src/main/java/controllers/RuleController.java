package controllers;

import dto.CreateRuleRequest;
import dto.RecommendationDto;
import exceptions.RuleNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import model.RecommendationRule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.RuleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rules")
@Tag(name = "Rule Management", description = "API для управления правилами рекомендаций")
public class RuleController {

    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    // Создать новое правило
    @PostMapping
    @Operation(summary = "Создать новое правило", description = "Создаёт новое правило на основе условий")
    @ApiResponse(responseCode = "201", description = "Правило успешно создано")
    public ResponseEntity<RecommendationRule> createRule(@RequestBody @Valid CreateRuleRequest request) {
        RecommendationRule rule = ruleService.createRule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(rule);
    }

    // Проверить, подходит ли пользователь под правило
    @GetMapping("/check/{ruleId}")
    @Operation(summary = "Проверить соответствие пользователя", description = "Проверяет, подходит ли пользователь под правило")
    public ResponseEntity<Boolean> checkUserEligibility(
            @Parameter(description = "ID правила", example = "147f6a0f-3b91-413b-ab99-87f081d60d5a") @PathVariable UUID ruleId,
            @Parameter(description = "ID пользователя", example = "cd515076-5d8a-44be-930e-8d4fcb79f42d") @RequestParam UUID userId) {
        boolean eligible = ruleService.isUserEligible(userId, ruleId);
        return ResponseEntity.ok(eligible);
    }

    // Получить все рекомендации для пользователя
    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<List<RecommendationDto>> getRecommendations(@PathVariable UUID userId) {
        List<RecommendationDto> recommendations = ruleService.getRecommendationsForUser(userId);
        return ResponseEntity.ok(recommendations);
    }

    //  Получить все правила
    @GetMapping
    public ResponseEntity<List<RecommendationRule>> getAllRules() {
        List<RecommendationRule> rules = ruleService.getAllRules();
        return ResponseEntity.ok(rules);
    }

    //  Удалить правило
    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Void> deleteRule(@PathVariable UUID ruleId) {
        try {
            ruleService.deleteRule(ruleId);
            return ResponseEntity.ok().build();
        } catch (RuleNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}