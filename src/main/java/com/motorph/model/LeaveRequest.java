package com.motorph.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
/**
 *
 * @author Lenovo
 */
public class LeaveRequest extends Request {
    
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveType leaveType;
    
    // Status inherited
    // approvedId inherited
    
    public LeaveRequest(String requestId,
                        String employeeId,
                        RequestStatus status,
                        String approverId,
                        String reason,
                        LocalDate dateFiled,
                        RequestType requestType,
                        LocalDate startDate,
                        LocalDate endDate,
                        LeaveType leaveType
                        ) {
        super(requestId, employeeId, status, approverId, reason, dateFiled, requestType);
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = leaveType;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public LeaveType getLeaveType() {
        return leaveType;
    }
    
    // Calculate number of leave days
    public long getLeaveDays() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
    
    
    // Calculates deduction amount (requires daily rate input)
    public double calculateImpact(double dailyRate) {
        return getLeaveDays() * dailyRate;
    }
    
    // Default overload
    public double calculateImpact() {
        return 0; // default
    }
    
    
   
}
