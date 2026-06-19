package com.motorph.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Maps to work_time_request where request_type = 'overtime'.
 *
 * DB alignment notes:
 *  - The DB stores start_time / end_time (TIME), not a pre-computed hours double.
 *    getHours() derives the duration at runtime from those two fields.
 *  - requestDate maps to work_time_request.request_date (DATE).
 *  - The inherited reason maps to work_time_request.reason (TEXT).
 */
public class OvertimeRequest extends Request {

    private final LocalDate overtimeDate;   // work_time_request.request_date
    private final LocalTime startTime;      // work_time_request.start_time
    private final LocalTime endTime;        // work_time_request.end_time

    public OvertimeRequest(int requestId,
                           int employeeId,
                           RequestStatus status,
                           Integer approverId,
                           String reason,
                           LocalDateTime dateFiled,
                           LocalDate overtimeDate,
                           LocalTime startTime,
                           LocalTime endTime) {
        super(requestId, employeeId, status, approverId, reason, dateFiled, RequestType.OVERTIME);
        this.overtimeDate = overtimeDate;
        this.startTime    = startTime;
        this.endTime      = endTime;
    }

    public LocalDate getOvertimeDate() { return overtimeDate; }
    public LocalTime getStartTime()    { return startTime; }
    public LocalTime getEndTime()      { return endTime; }

    /** Derived: duration in fractional hours between startTime and endTime. */
    public double getHours() {
        long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
        return minutes / 60.0;
    }

    /** Overtime adds to salary. */
    public double calculateImpact(double hourlyRate) {
        return getHours() * hourlyRate;
    }
}