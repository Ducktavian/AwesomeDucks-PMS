package com.motorph.service;

import com.motorph.dao.AttendanceDAO;
import com.motorph.model.Attendance;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AttendanceService {

    // attendance_status_id = 1 ("Present") from seed data
    private static final int STATUS_PRESENT = 1;

    private final AttendanceDAO attendanceDAO;

    public AttendanceService(AttendanceDAO attendanceDAO) {
        this.attendanceDAO = attendanceDAO;
    }

    // -----------------------------------------------------------------------
    // Time-in / Time-out
    // -----------------------------------------------------------------------

    /**
     * Records a time-in for today.
     * Throws IllegalStateException if the employee has already timed in today
     * and has not yet timed out.
     */
    public Attendance timeIn(String employeeId) {
        Attendance open = attendanceDAO.findOpenSession(employeeId);
        if (open != null) {
            throw new IllegalStateException(
                    "Employee " + employeeId + " has already timed in today at " + open.getLogIn() + ".");
        }
        Attendance record = new Attendance(employeeId, LocalDate.now(), LocalTime.now());
        record.setAttendanceStatusId(STATUS_PRESENT);
        attendanceDAO.save(record);
        return record;
    }

    /**
     * Records a time-out for today's open session.
     * Throws IllegalStateException if the employee has not timed in today.
     */
    public Attendance timeOut(String employeeId) {
        Attendance open = attendanceDAO.findOpenSession(employeeId);
        if (open == null) {
            throw new IllegalStateException(
                    "Employee " + employeeId + " has not timed in today.");
        }
        open.setLogOut(LocalTime.now());
        attendanceDAO.update(open);
        return open;
    }

    // -----------------------------------------------------------------------
    // Hours computation
    // -----------------------------------------------------------------------

    /** Computes hours worked for a single attendance record. */
    public double computeDailyHours(Attendance record) {
        if (record.getLogIn() == null || record.getLogOut() == null) {
            return 0.0;
        }
        double minutes = Duration.between(record.getLogIn(), record.getLogOut()).toMinutes();
        return Math.round((minutes / 60.0) * 100.0) / 100.0;
    }

    /** Computes total hours worked for an employee across all records. */
    public double computeTotalHours(String employeeId) {
        List<Attendance> records = attendanceDAO.findByEmployeeId(employeeId);
        double total = 0;
        for (Attendance record : records) {
            total += computeDailyHours(record);
        }
        return Math.round(total * 100.0) / 100.0;
    }

    /** Computes total hours worked within a pay period. */
    public double computeTotalHours(String employeeId, LocalDate periodStart, LocalDate periodEnd) {
        List<Attendance> records = attendanceDAO.findByEmployeeId(employeeId);
        double total = 0;
        for (Attendance record : records) {
            LocalDate date = record.getDate();
            if (!date.isBefore(periodStart) && !date.isAfter(periodEnd)) {
                total += computeDailyHours(record);
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    // -----------------------------------------------------------------------
    // Retrieval
    // -----------------------------------------------------------------------

    public List<Attendance> getAllAttendance() {
        return attendanceDAO.findAll();
    }

    public List<Attendance> getAllAttendance(String employeeId) {
        return attendanceDAO.findByEmployeeId(employeeId);
    }

    public Attendance findById(String attendanceId) {
        return attendanceDAO.findById(attendanceId);
    }
}
