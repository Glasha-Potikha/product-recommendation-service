package dto;

import java.math.BigDecimal;
import java.util.List;

public class ConditionRequest {
    private String type;
    private String productType;
    private String transactionType;
    private BigDecimal threshold;
    private String comparator;
    private Boolean shouldExist;
    private List<ConditionRequest> nestedConditions;

    // Геттеры
    public String getType() { return type; }
    public String getProductType() { return productType; }
    public String getTransactionType() { return transactionType; }
    public BigDecimal getThreshold() { return threshold; }
    public String getComparator() { return comparator; }
    public Boolean getShouldExist() { return shouldExist; }
    public List<ConditionRequest> getNestedConditions() { return nestedConditions; }

    public void setType(String type) { this.type = type; }
    public void setProductType(String productType) { this.productType = productType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public void setThreshold(BigDecimal threshold) { this.threshold = threshold; }
    public void setComparator(String comparator) { this.comparator = comparator; }
    public void setShouldExist(Boolean shouldExist) { this.shouldExist = shouldExist; }
    public void setNestedConditions(List<ConditionRequest> nestedConditions) { this.nestedConditions = nestedConditions; }
}