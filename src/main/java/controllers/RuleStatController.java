package controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import dto.RuleStatResponse;
import service.RuleStatService;

import java.util.List;

@RestController
@RequestMapping("/api/rule-stats")
@RequiredArgsConstructor
public class RuleStatController {
    private final RuleStatService ruleStatService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RuleStatResponse> getAllStats() {
        return ruleStatService.getAllStats();
    }
}