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
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    // Создать новое правило
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)  // Здесь автоматически возвращает статус 201 при успешном выполнении
    @Operation(summary = "Создать новое правило", description = "Создаёт новое правило на основе условий")
    @ApiResponse(responseCode = "201", description = "Правило успешно создано")
    public RecommendationRule createRule(@RequestBody @Valid CreateRuleRequest request) {
        //Далее возвращаем созданное правило напрямую, Spring обрабатывает статус через @ResponseStatus
        return ruleService.createRule(request);
    }

    // Проверить, подходит ли пользователь под правило
    @GetMapping("/check/{ruleId}")
    @Operation(summary = "Проверить соответствие пользователя",
            description = "Проверяет, подходит ли пользователь под правило")
    public boolean checkUserEligibility(
            @Parameter(description = "ID правила", example = "147f6a0f-3b91-413b-ab99-87f081d60d5a")
            @PathVariable UUID ruleId,
            @Parameter(description = "ID пользователя", example = "cd515076-5d8a-44be-930e-8d4fcb79f42d")
            @RequestParam UUID userId) {
        // Возвращаем boolean напрямую, Spring по умолчанию использует статус 200 OK
        return ruleService.isUserEligible(userId, ruleId);
    }

    // Получить все рекомендации для пользователя
    @GetMapping("/recommendations/{userId}")
    @ResponseStatus(HttpStatus.OK)  // Явно указываем статус для ясности
    public List<RecommendationDto> getRecommendations(@PathVariable UUID userId) {
        // Возвращаем список рекомендаций напрямую
        return ruleService.getRecommendationsForUser(userId);
    }

    // Получить все правила
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RecommendationRule> getAllRules() {
        // Возвращаем все правила без обертки в ResponseEntity, дороботка от Антона
        return ruleService.getAllRules();
    }

    // Удалить правило по ID продукта
    @DeleteMapping("/by-product/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)  // 204 No Content для успешного удаления
    @Operation(summary = "Удалить правило по product_id",
            description = "Удаляет все правила рекомендаций для указанного продукта")
    @ApiResponse(responseCode = "204", description = "Правила успешно удалены")
    @ApiResponse(responseCode = "404", description = "Правила для данного продукта не найдены")
    public void deleteRuleByProductId(
            @Parameter(description = "ID продукта", example = "ab138afb-f3ba-4a93-b74f-0fcee86d447f")
            @PathVariable UUID productId) throws RuleNotFoundException {
        // Выбрасываем исключение, которое будет обработано глобальным обработчиком (ControllerAdvice)
        ruleService.deleteRuleByProductId(productId);
    }
}