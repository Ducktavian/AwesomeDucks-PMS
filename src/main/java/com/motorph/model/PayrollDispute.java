package com.motorph.model;

import java.time.LocalDate;

public class PayrollDispute extends Dispute {

    private String payslipNumber; // dispute.payslip_number (e.g. "PS-2024-07-0001")

    public PayrollDispute(int disputeId, String employeeId, String reason,
                          DisputeStatus status, Integer reviewedById,
                          LocalDate dateFiled, LocalDate dateReviewed,
                          String payslipNumber) {
        super(disputeId, employeeId, reason, status, reviewedById,
              dateFiled, dateReviewed, DisputeType.PAYROLL_DISPUTE);
        this.payslipNumber = payslipNumber;
    }

    public String getPayslipNumber() { return payslipNumber; }
}
