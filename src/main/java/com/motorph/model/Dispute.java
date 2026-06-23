package com.motorph.model;

import java.time.LocalDate;

public abstract class Dispute implements Disputable {

    protected int disputeId;
    protected String employeeId;
    protected String reason;
    protected DisputeStatus status;
    protected Integer reviewedById;
    protected LocalDate dateFiled;
    protected LocalDate dateReviewed;
    protected DisputeType disputeType;

    public Dispute(int disputeId, String employeeId, String reason,
                   DisputeStatus status, Integer reviewedById,
                   LocalDate dateFiled, LocalDate dateReviewed,
                   DisputeType disputeType) {
        this.disputeId = disputeId;
        this.employeeId = employeeId;
        this.reason = reason;
        this.status = status;
        this.reviewedById = reviewedById;
        this.dateFiled = dateFiled;
        this.dateReviewed = dateReviewed;
        this.disputeType = disputeType;
    }

    @Override public String getDisputeId() { return String.valueOf(disputeId); }
    public int getDisputeIntId() { return disputeId; }
    @Override public String getEmployeeId() { return employeeId; }
    @Override public String getReason() { return reason; }
    @Override public DisputeStatus getStatus() { return status; }
    @Override public Integer getReviewedById() { return reviewedById; }
    @Override public LocalDate getDateFiled() { return dateFiled; }
    public LocalDate getDateReviewed() { return dateReviewed; }
    @Override public DisputeType getDisputeType() { return disputeType; }

    public void setDisputeId(int disputeId) { this.disputeId = disputeId; }
    public void setStatus(DisputeStatus status) { this.status = status; }
    public void setReviewedById(Integer reviewedById) { this.reviewedById = reviewedById; }
    public void setDateReviewed(LocalDate dateReviewed) { this.dateReviewed = dateReviewed; }
}
