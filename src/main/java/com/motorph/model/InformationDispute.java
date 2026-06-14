package com.motorph.model;

import java.time.LocalDate;

/**
 *
 * @author Ducktavian
 */
public class InformationDispute extends Dispute {
    
    private String category;
    private String targetField;

    public InformationDispute(String category, String targetField, String disputeId, String employeeId, String reason, DisputeStatus status, String reviewedById, LocalDate dateFiled, LocalDate dateReviewed, DisputeType disputeType) {
        super(disputeId, employeeId, reason, status, reviewedById, dateFiled, dateReviewed, disputeType);
        this.category = category;
        this.targetField = targetField;
    }

    public String getCategory() {
        return category;
    }

    public String getTargetField() {
        return targetField;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }
    
    
}
