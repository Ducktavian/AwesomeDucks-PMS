package com.motorph.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class OvertimeRequest extends Request {

    private final LocalDate overtimeDate;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public OvertimeRequest(int requestId, int employeeId, RequestStatus status,
                           Integer approverId, String reason, LocalDateTime dateFiled,
                           LocalDate overtimeDate, LocalTime startTime, LocalTime endTime) {
        super(requestId, employeeId, status, approverId, reason, dateFiled, RequestType.OVERTIME);
        this.overtimeDate = overtimeDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDate getOvertimeDate() { return overtimeDate; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }

    public double getHours() {
        long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
        return minutes / 60.0;
    }

    public double calculateImpact(double hourlyRate) {
        return getHours() * hourlyRate;
    }
}
