
package com.motorph.service;

import com.motorph.model.*;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Lenovo
 */
public class PayrollDisputeService {
    
    private List<PayrollDispute> disputes = new ArrayList<>();
    
    // FILE DISPUTE
    public PayrollDispute fileDispute(UserAccount user, Payslip payslip, String reason) {
        
        if (!payslip.getEmployeeNumber().equals(user.getEmployeeNumber())) {
            throw new IllegalArgumentException("You can only dispute your own payslip.");
        }
        
        String disputeId = UUID.randomUUID().toString();
        
        PayrollDispute dispute = new PayrollDispute(
                disputeId, 
                user.getEmployeeNumber(),
                payslip.getPayslipId(),
                reason       
        ); 
        
        disputes.add(dispute);
        
        return dispute;
    }
    
    // APPROVE DISPUTE
    public void approveDispute(PayrollDispute dispute, UserAccount user) {
        
        if (!(user.getRole() == Role.HR || user.getRole() == Role.FINANCE)) {
            throw new SecurityException("Only HR or Finance can approve disputes.");
            
        }
        
        if (dispute.getStatus() != DisputeStatus.PENDING) {
            throw new IllegalStateException("Dispute already reviewed.");
        }
        
        dispute.setStatus(DisputeStatus.APPROVED);
        dispute.setReviewedBy(user.getEmployeeNumber());
        dispute.setDateReviewed(LocalDate.now());
    }
    
    // REJECT DISPUTE
    public void rejectDispute(PayrollDispute dispute, UserAccount user) {
        
        if (!(user.getRole() == Role.HR || user.getRole() == Role.FINANCE)) {
            throw new SecurityException("Only HR or Finance can reject disputes.");
            
        }
        
        if (dispute.getStatus() != DisputeStatus.PENDING) {
            throw new IllegalStateException("Dispute already reviewed.");
        }
        
        dispute.setStatus(DisputeStatus.DENIED);
        dispute.setReviewedBy(user.getEmployeeNumber());
        dispute.setDateReviewed(LocalDate.now());
    }
    
    // VIEW ALL DISPUTES
    public List<PayrollDispute> getAllDisputes() {
        return disputes;
    }
}
