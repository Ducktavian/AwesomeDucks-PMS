package com.motorph.model;

import java.time.LocalDate;

public class InformationDispute extends Dispute {

    private String category;    // dispute.category
    private String targetField; // dispute.target_field

    public InformationDispute(int disputeId, String employeeId, String reason,
                              DisputeStatus status, Integer reviewedById,
                              LocalDate dateFiled, LocalDate dateReviewed,
                              String category, String targetField) {
        super(disputeId, employeeId, reason, status, reviewedById,
              dateFiled, dateReviewed, DisputeType.INFORMATION_DISPUTE);
        this.category    = category;
        this.targetField = targetField;
    }

    public String getCategory()    { return category; }
    public String getTargetField() { return targetField; }

    public void setCategory(String category)       { this.category = category; }
    public void setTargetField(String targetField) { this.targetField = targetField; }
}
