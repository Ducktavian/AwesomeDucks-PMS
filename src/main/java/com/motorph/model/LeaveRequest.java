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
    private String reason;
    private LeaveType leaveType;
    
    // Status inherited
    // approvedBy inherited
    
    public LeaveRequest(String requestId,
                        String employeeNumber,
                        LocalDate dateFiled,
                        LocalDate startDate,
                        LocalDate endDate,
                        LeaveType leaveType,
                        String reason
                        ) {
        super(requestId, employeeNumber, dateFiled);
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.leaveType = leaveType;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public String getReason() {
        return reason;
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
