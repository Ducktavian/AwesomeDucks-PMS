package com.motorph.model;

import java.time.LocalDate;

/**
 *
 * @author Ducktavian
 */
public abstract class Dispute implements Disputable {
    protected String disputeId;
    protected String employeeId;
    protected String reason;
    protected DisputeStatus status;
    protected String reviewedById;
    protected LocalDate dateFiled;
    protected LocalDate dateReviewed;
    protected DisputeType disputeType;

    public Dispute(String disputeId, String employeeId, String reason, DisputeStatus status, String reviewedById, LocalDate dateFiled, LocalDate dateReviewed, DisputeType disputeType) {
        this.disputeId = disputeId;
        this.employeeId = employeeId;
        this.reason = reason;
        this.status = status;
        this.reviewedById = reviewedById;
        this.dateFiled = dateFiled;
        this.dateReviewed = dateReviewed;
        this.disputeType = disputeType;
    }

    @Override
    public String getDisputeId() {
        return disputeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getReason() {
        return reason;
    }
    
    @Override
    public DisputeStatus getStatus() {
        return status;
    }
    
    @Override
    public String getReviewedById() {
        return reviewedById;
    }
    
    @Override
    public LocalDate getDateFiled() {
        return dateFiled;
    }

    public LocalDate getDateReviewed() {
        return dateReviewed;
    }
    
    @Override
    public DisputeType getDisputeType() {
        return disputeType;
    }
    
    
    
}
