

package com.motorph.model;

import java.time.LocalDate;
/**
 *
 * @author Lenovo
 */
public class UndertimeRequest extends Request{
    
    private LocalDate undertimeDate;
    private double hours;
    
    public UndertimeRequest(String requestId,
                            String employeeId,
                            RequestStatus status,
                            String approverId,
                            String reason,
                            LocalDate dateFiled,
                            RequestType requestType,
                            LocalDate undertimeDate,
                            double hours
    ) {
        super(requestId, employeeId, status, approverId, reason, dateFiled, requestType);
        this.undertimeDate = undertimeDate;
        this.hours = hours;
    }
    
    public LocalDate getUndertimeDate() {
        return undertimeDate;
    }
    
    public double getHours() {
        return hours;
    }
    
    public String getReason() {
        return reason;
    }
    
    public double calculateImpact(double hourlyRate) {
        // Negative: reduces salary
        return -(hours * hourlyRate);
    }
}
