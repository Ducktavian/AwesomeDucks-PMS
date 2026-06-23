package com.motorph.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class LeaveRequest extends Request {

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LeaveType leaveType;

    public LeaveRequest(int requestId, int employeeId, RequestStatus status,
                        Integer approverId, String reason, LocalDateTime dateFiled,
                        LocalDate startDate, LocalDate endDate, LeaveType leaveType) {
        super(requestId, employeeId, status, approverId, reason, dateFiled, RequestType.LEAVE);
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = leaveType;
    }

    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LeaveType getLeaveType() { return leaveType; }

    public long getLeaveDays() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    public double calculateImpact(double dailyRate) {
        return getLeaveDays() * dailyRate;
    }
}
