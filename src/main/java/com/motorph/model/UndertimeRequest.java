package com.motorph.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class UndertimeRequest extends Request {

    private final LocalDate undertimeDate;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public UndertimeRequest(int requestId, int employeeId, RequestStatus status,
                            Integer approverId, String reason, LocalDateTime dateFiled,
                            LocalDate undertimeDate, LocalTime startTime, LocalTime endTime) {
        super(requestId, employeeId, status, approverId, reason, dateFiled, RequestType.UNDERTIME);
        this.undertimeDate = undertimeDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDate getUndertimeDate() { return undertimeDate; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }

    public double getHours() {
        long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
        return minutes / 60.0;
    }

    public double calculateImpact(double hourlyRate) {
        return -(getHours() * hourlyRate);
    }
}
