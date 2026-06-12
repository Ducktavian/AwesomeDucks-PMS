
package com.motorph.model;

import java.time.LocalDate;

/**
 *
 * @author Lenovo
 */
public class PayrollDispute {
    
    private String disputeId;
    private String employeeNumber;
    private String payslipId;
    private String reason;
    private DisputeStatus status;
    private String reviewedBy;
    private LocalDate dateFiled;
    private LocalDate dateReviewed;
    
    public PayrollDispute(String disputeId, String employeeNumber, String payslipId, String reason) {
        this.disputeId = disputeId;
        this.employeeNumber = employeeNumber;
        this.payslipId = payslipId;
        this.reason = reason;
        this.status = DisputeStatus.PENDING;
        this.dateFiled = LocalDate.now();
    }
    
    // Getters

    public String getDisputeId() {
        return disputeId;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public String getPayslipId() {
        return payslipId;
    }

    public String getReason() {
        return reason;
    }

    public DisputeStatus getStatus() {
        return status;
    }

    public String getReviewBy() {
        return reviewedBy;
    }

    public LocalDate getDateFiled() {
        return dateFiled;
    }

    public LocalDate getDateReviewd() {
        return dateReviewed;
    }
    
    // Setters
    public void setStatus(DisputeStatus status) {
        this.status = status;
    }
    
    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
    
    public void setDateReviewed(LocalDate dateReviewed) {
        this.dateReviewed = dateReviewed;
    }
}
