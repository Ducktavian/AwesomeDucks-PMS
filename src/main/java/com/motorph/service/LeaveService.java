

package com.motorph.service;

import com.motorph.dao.LeaveDAO;
import com.motorph.model.LeaveRequest;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class LeaveService {
    
    private LeaveDAO leaveDAO;
    
    public LeaveService(LeaveDAO leaveDAO) {
        this.leaveDAO = leaveDAO;
    }
    
    public void approveLeave(LeaveRequest leave, String approverId) {
        leave.approve(approverId);
        leaveDAO.updateRequest(leave);
    }
    
    public void denyLeave(LeaveRequest leave, String approverId) {
        leave.deny(approverId);
        leaveDAO.updateRequest(leave);
    }
    
    public void submitLeaveRequest(LeaveRequest request) {
        validateLeaveRequest(request);
        leaveDAO.submitLeaveRequest(request);
    }
    
    public List<LeaveRequest> getAllLeave() {
        return leaveDAO.getAllRequests();
    }
    
    public List<LeaveRequest> getAllLeave(String employeeNumber) {
        return leaveDAO.getRequestsByEmployee(employeeNumber);
    }
    
    public String generateNextLeaveId(String employeeNumber) {
        List<LeaveRequest> allLeaves = getAllLeave(employeeNumber);
        return leaveDAO.generateNextLeaveId(employeeNumber, allLeaves);
    }
    
    private void validateLeaveRequest(LeaveRequest request) {
        LocalDate today = LocalDate.now();
        
        if (request.getStartDate().isBefore(today)) {
            throw new IllegalArgumentException("Start date cannot be in the past.");
        }
        
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be earlier than the start date.");
        }
    }
    
    public LeaveRequest findById(String requestId) {
        return leaveDAO.findById(requestId);
    }
    
    
}
