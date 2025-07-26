package ruleset.parser;

import lombok.RequiredArgsConstructor;
import repository.TransactionRepository;
import ruleset.condition.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class TextRuleParser {
    private final TransactionRepository repository;

    //разбор простых SQL запросов, без скобок
    public Condition parse(String ruleText) {
        if (ruleText.contains(" AND ")) {
            String[] parts = ruleText.split(" AND ");
            List<Condition> conditions = Arrays.stream(parts)
                    .map(this::parseSingleCondition)
                    .toList();
            return new AndCondition(conditions);
        } else if (ruleText.contains(" OR ")) {
            String[] parts = ruleText.split(" OR ");
            List<Condition> conditions = Arrays.stream(parts)
                    .map(this::parseSingleCondition)
                    .toList();
            return new OrCondition(conditions);
        } else {
            return parseSingleCondition(ruleText);
        }
    }

    private Condition parseSingleCondition(String text) {
        text = text.trim();
        if (text.endsWith("_EXISTS")) {
            String type = text.replace("_EXISTS", "");
            return new ProductExistsCondition(repository, type, true);
        } else if (text.endsWith("_NOT_EXISTS")) {
            String type = text.replace("_NOT_EXISTS", "");
            return new ProductExistsCondition(repository, type, false);
        } else if (text.matches(".*(>|>=|<|<=).*")) {
            String[] parts = text.split("(>=|<=|>|<)");
            String left = parts[0].trim();
            String comparator = text.replaceAll("[^><=]", "");
            BigDecimal value = new BigDecimal(parts[1].trim());

            String[] tokens = left.split("_");
            return new TransactionSumCondition(repository, tokens[0], tokens[1], value, comparator);
        }
        throw new IllegalArgumentException("Незнакомое правило: " + text);
    }
}
