package com.motorph.model;

import java.time.LocalDate;
/**
 *
 * @author Lenovo
 */
public class OvertimeRequest extends Request{
    
    private LocalDate overtimeDate;
    private double hours;
    
    public OvertimeRequest(String requestId,
                           String employeeId,
                           RequestStatus status,
                           String approverId,
                           String reason,
                           LocalDate dateFiled,
                           RequestType requestType,
                           LocalDate overtimeDate,
                           double hours) {
        super(requestId, employeeId, status, approverId, reason, dateFiled, requestType);
        this.overtimeDate = overtimeDate;
        this.hours = hours;
        
    }
    
    public LocalDate getOvertimeDate(){
        return overtimeDate;
    }
    
    public double getHours() {
        return hours;
    }
    

    public double calculateImpact(double hourlyRate) {
        return hours * hourlyRate;
    }
}
