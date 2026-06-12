

package com.motorph.model;

import java.time.LocalDate;
/**
 *
 * @author Lenovo
 */
public class UndertimeRequest extends Request{
    
    private LocalDate undertimeDate;
    private double hours;
    private String reason;
    
    public UndertimeRequest(String requestId,
                            String employeeId,
                            LocalDate dateFiled,
                            LocalDate undertimeDate,
                            double hours,
                            String reason) {
        super(requestId, employeeId, dateFiled);
        this.undertimeDate = undertimeDate;
        this.hours = hours;
        this.reason = reason;
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
    
    @Override
    public double calculateImpact(double hourlyRate) {
        // Negative: reduces salary
        return -(hours * hourlyRate);
    }
}
