
package com.motorph.model;

import java.time.LocalDate;

/**
 *
 * @author Lenovo
 */
public class PayrollDispute extends Dispute {
    
    private String payslipId;
    
    public PayrollDispute(String payslipId, String disputeId, String employeeId, String reason, DisputeStatus status, String reviewedById, LocalDate dateFiled, LocalDate dateReviewed, DisputeType disputeType) {
        super(disputeId, employeeId, reason, status, reviewedById, dateFiled, dateReviewed, disputeType);
        this.payslipId = payslipId;
    }
    
    // Getters

    public String getPayslipId() {
        return payslipId;
    }
}
