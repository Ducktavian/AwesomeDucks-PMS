package com.motorph.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Maps to work_time_request where request_type = 'undertime'.
 *
 * DB alignment notes:
 *  - Same table as OvertimeRequest (work_time_request), differentiated by
 *    request_type = 'undertime'.
 *  - start_time / end_time represent the period the employee left early.
 *    getHours() computes the undertime duration from those fields.
 */
public class UndertimeRequest extends Request {

    private final LocalDate undertimeDate;  // work_time_request.request_date
    private final LocalTime startTime;      // work_time_request.start_time
    private final LocalTime endTime;        // work_time_request.end_time

    public UndertimeRequest(int requestId,
                            int employeeId,
                            RequestStatus status,
                            Integer approverId,
                            String reason,
                            LocalDateTime dateFiled,
                            LocalDate undertimeDate,
                            LocalTime startTime,
                            LocalTime endTime) {
        super(requestId, employeeId, status, approverId, reason, dateFiled, RequestType.UNDERTIME);
        this.undertimeDate = undertimeDate;
        this.startTime     = startTime;
        this.endTime       = endTime;
    }

    public LocalDate getUndertimeDate() { return undertimeDate; }
    public LocalTime getStartTime()     { return startTime; }
    public LocalTime getEndTime()       { return endTime; }

    /** Derived: duration in fractional hours. */
    public double getHours() {
        long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
        return minutes / 60.0;
    }

    /** Undertime reduces salary — returns a negative value. */
    public double calculateImpact(double hourlyRate) {
        return -(getHours() * hourlyRate);
    }
}