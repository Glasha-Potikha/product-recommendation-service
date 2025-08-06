package dto;

import java.util.List;
import java.util.UUID;

public class CreateRuleRequest {

    private UUID productId;
    private String productName;
    private String productDescription;
    private List<ConditionRequest> conditions;

    // Геттеры
    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public List<ConditionRequest> getConditions() {
        return conditions;
    }
    public void setProductId(UUID productId) {
        this.productId = productId;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
    public void setConditions(List<ConditionRequest> conditions) {
        this.conditions = conditions;
    }


}