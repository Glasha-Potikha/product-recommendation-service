package dto;

import java.util.UUID;

public record RuleStatResponse(
        UUID ruleId,
        String ruleName,
        Long count
) {}