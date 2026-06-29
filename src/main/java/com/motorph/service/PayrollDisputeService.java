package com.motorph.service;

import com.motorph.dao.DisputeDAO;
import com.motorph.model.*;
import com.motorph.util.Session;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PayrollDisputeService {

    private final DisputeDAO disputeDAO;

    public PayrollDisputeService(DisputeDAO disputeDAO) {
        this.disputeDAO = disputeDAO;
    }

    
    // Files a payroll dispute for a specific payslip.
    // The logged-in employee may only dispute their own payslip.
    public PayrollDispute fileDispute(Payslip payslip, String reason) {
        UserAccount current = Session.getCurrentUser();
        if (current == null) {
            throw new IllegalStateException("No active session.");
        }
        if (!payslip.getEmployeeNumber().equals(String.valueOf(current.getEmployeeId()))) {
            throw new IllegalArgumentException("You can only dispute your own payslip.");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason is required.");
        }

        PayrollDispute dispute = new PayrollDispute(
                0, // DB assigns the ID on INSERT
                String.valueOf(current.getEmployeeId()),
                reason,
                DisputeStatus.UNRESOLVED,
                null,
                LocalDate.now(),
                null,
                payslip.getPayslipId()
        );
        disputeDAO.save(dispute);
        return dispute;
    }

    
    // Marks a payroll dispute as resolved.
    // Only Finance, HR, or Admin may do this.
    public void resolveDispute(PayrollDispute dispute) {
        UserAccount reviewer = Session.getCurrentUser();
        if (reviewer == null) {
            throw new IllegalStateException("No active session.");
        }
        Role role = reviewer.getRole();
        if (role != Role.FINANCE
                && role != Role.HR
                && role != Role.ADMIN) {
            throw new SecurityException(
                    "Only Finance, HR, or Admin can resolve payroll disputes.");
        }
        if (dispute.getStatus() == DisputeStatus.RESOLVED) {
            throw new IllegalStateException("Dispute is already resolved.");
        }

        dispute.setStatus(DisputeStatus.RESOLVED);
        dispute.setReviewedById(reviewer.getEmployeeId());
        dispute.setDateReviewed(LocalDate.now());
        disputeDAO.update(dispute);
    }


    // Files a payroll dispute by payslip number string (no Payslip object required).
    public PayrollDispute fileDisputeByPayslipNumber(String payslipNumber, String reason) {
        UserAccount current = Session.getCurrentUser();
        if (current == null) throw new IllegalStateException("No active session.");
        if (payslipNumber == null || payslipNumber.isBlank())
            throw new IllegalArgumentException("Payslip number is required.");
        if (reason == null || reason.isBlank())
            throw new IllegalArgumentException("Reason is required.");

        PayrollDispute dispute = new PayrollDispute(
                0, String.valueOf(current.getEmployeeId()), reason,
                DisputeStatus.UNRESOLVED, null, LocalDate.now(), null, payslipNumber);
        disputeDAO.save(dispute);
        return dispute;
    }


    // Retrieval
    public List<PayrollDispute> findAll() {
        return disputeDAO.findAll().stream()
                .filter(d -> d instanceof PayrollDispute)
                .map(d -> (PayrollDispute) d)
                .collect(Collectors.toList());
    }

    public List<PayrollDispute> findByEmployee(String employeeId) {
        return disputeDAO.findByEmployeeId(employeeId).stream()
                .filter(d -> d instanceof PayrollDispute)
                .map(d -> (PayrollDispute) d)
                .collect(Collectors.toList());
    }

    public List<PayrollDispute> findUnresolved() {
        return disputeDAO.findByStatus(DisputeStatus.UNRESOLVED).stream()
                .filter(d -> d instanceof PayrollDispute)
                .map(d -> (PayrollDispute) d)
                .collect(Collectors.toList());
    }

    public Dispute findById(String disputeId) {
        return disputeDAO.findById(disputeId);
    }
}
