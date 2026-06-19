package com.motorph.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Maps to the leave_request table.
 *
 * DB alignment notes:
 *  - leaveType is now a LeaveType object (holds leaveTypeId + leaveTypeName)
 *    because the DB stores leave types in a normalised lookup table, not an enum.
 *  - The inherited reason maps to leave_request.description (TEXT).
 *  - dateFiled (inherited) maps to leave_request.created_at.
 */
public class LeaveRequest extends Request {

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LeaveType leaveType;

    public LeaveRequest(int requestId,
                        int employeeId,
                        RequestStatus status,
                        Integer approverId,
                        String reason,
                        LocalDateTime dateFiled,
                        LocalDate startDate,
                        LocalDate endDate,
                        LeaveType leaveType) {
        super(requestId, employeeId, status, approverId, reason, dateFiled, RequestType.LEAVE);
        this.startDate = startDate;
        this.endDate   = endDate;
        this.leaveType = leaveType;
    }

    public LocalDate getStartDate()  { return startDate; }
    public LocalDate getEndDate()    { return endDate; }
    public LeaveType getLeaveType()  { return leaveType; }

    /** Number of calendar days covered by this leave request (inclusive). */
    public long getLeaveDays() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    /**
     * Total salary deduction for this leave period.
     *
     * @param dailyRate employee's daily rate
     */
    public double calculateImpact(double dailyRate) {
        return getLeaveDays() * dailyRate;
    }
}