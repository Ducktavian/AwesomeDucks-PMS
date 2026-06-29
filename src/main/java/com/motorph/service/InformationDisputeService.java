package com.motorph.service;

import com.motorph.dao.DisputeDAO;
import com.motorph.model.*;
import com.motorph.util.Session;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class InformationDisputeService {

    private final DisputeDAO disputeDAO;

    public InformationDisputeService(DisputeDAO disputeDAO) {
        this.disputeDAO = disputeDAO;
    }

    
    //Files a new information dispute on behalf of the currently logged-in employee.
    // category  — e.g. "Personal Info", "Government ID"
    // targetField — the specific field being disputed, e.g. "philhealth_number"
    public InformationDispute fileDispute(String reason, String category, String targetField) {
        UserAccount current = Session.getCurrentUser();
        if (current == null) {
            throw new IllegalStateException("No active session.");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason is required.");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category is required.");
        }
        if (targetField == null || targetField.isBlank()) {
            throw new IllegalArgumentException("Target field is required.");
        }

        InformationDispute dispute = new InformationDispute(
                0, // DB assigns the ID on INSERT
                String.valueOf(current.getEmployeeId()),
                reason,
                DisputeStatus.UNRESOLVED,
                null,
                LocalDate.now(),
                null,
                category,
                targetField
        );
        disputeDAO.save(dispute);
        return dispute;
    }

   
    // Marks a dispute as resolved. HR, IT, or Admin may do this.
    public void resolveDispute(InformationDispute dispute) {
        UserAccount reviewer = Session.getCurrentUser();
        if (reviewer == null) {
            throw new IllegalStateException("No active session.");
        }
        Role role = reviewer.getRole();
        if (role != Role.HR && role != Role.ADMIN && role != Role.IT) {
            throw new SecurityException("Only HR, IT, or Admin can resolve information disputes.");
        }
        if (dispute.getStatus() == DisputeStatus.RESOLVED) {
            throw new IllegalStateException("Dispute is already resolved.");
        }

        dispute.setStatus(DisputeStatus.RESOLVED);
        dispute.setReviewedById(reviewer.getEmployeeId());
        dispute.setDateReviewed(LocalDate.now());
        disputeDAO.update(dispute);
    }

    // Deletes a dispute record. Admin only.
    public void deleteDispute(String disputeId) {
        UserAccount current = Session.getCurrentUser();
        if (current == null || current.getRole() != Role.ADMIN) {
            throw new SecurityException("Only Admin can delete disputes.");
        }
        disputeDAO.delete(disputeId);
    }


    // Retrieval
    public List<InformationDispute> findAll() {
        return disputeDAO.findAll().stream()
                .filter(d -> d instanceof InformationDispute)
                .map(d -> (InformationDispute) d)
                .collect(Collectors.toList());
    }

    public List<InformationDispute> findByEmployee(String employeeId) {
        return disputeDAO.findByEmployeeId(employeeId).stream()
                .filter(d -> d instanceof InformationDispute)
                .map(d -> (InformationDispute) d)
                .collect(Collectors.toList());
    }

    public List<InformationDispute> findUnresolved() {
        return disputeDAO.findByStatus(DisputeStatus.UNRESOLVED).stream()
                .filter(d -> d instanceof InformationDispute)
                .map(d -> (InformationDispute) d)
                .collect(Collectors.toList());
    }

    public Dispute findById(String disputeId) {
        return disputeDAO.findById(disputeId);
    }
}
