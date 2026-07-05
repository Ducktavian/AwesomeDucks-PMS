package com.motorph.service;

import com.motorph.dao.AttendanceDAO;
import com.motorph.model.Attendance;
import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import com.motorph.util.Session;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AttendanceService {

    // attendance_status seed ids — see `attendance_status` seed data.
    private static final int STATUS_PRESENT  = 1;
    private static final int STATUS_ABSENT   = 2;
    private static final int STATUS_LATE     = 3;
    private static final int STATUS_HALF_DAY = 4;
    private static final int STATUS_ON_LEAVE = 5;

    private final AttendanceDAO attendanceDAO;

    public AttendanceService(AttendanceDAO attendanceDAO) {
        this.attendanceDAO = attendanceDAO;
    }

    public Attendance timeIn(String employeeId) {
        validateEmployeeId(employeeId);

        Attendance open = attendanceDAO.findOpenSession(employeeId);

        if (open != null) {
            throw new IllegalStateException(
                    "Employee " + employeeId + " has already timed in today at " + open.getLogIn() + "."
            );
        }

        Attendance record = new Attendance(employeeId, LocalDate.now(), LocalTime.now());
        record.setAttendanceStatusId(STATUS_PRESENT);
        attendanceDAO.save(record);

        return record;
    }

    public Attendance timeOut(String employeeId) {
        validateEmployeeId(employeeId);

        Attendance open = attendanceDAO.findOpenSession(employeeId);

        if (open == null) {
            throw new IllegalStateException("Employee " + employeeId + " has not timed in today.");
        }

        open.setLogOut(LocalTime.now());
        attendanceDAO.update(open);

        return open;
    }

    // NEW
    public Attendance getTodaysAttendance(String employeeId) {
        validateEmployeeId(employeeId);

        LocalDate today = LocalDate.now();
        for (Attendance record : attendanceDAO.findByEmployeeId(employeeId)) {
            if (today.equals(record.getDate())) {
                return record;
            }
        }
        return null;
    }

    public void submitAttendance(Attendance attendance) {
        validateAttendanceForCurrentUser(attendance);
        attendanceDAO.save(attendance);
    }

    public void updateAttendance(Attendance attendance) {
        validateAttendanceForCurrentUser(attendance);
        attendanceDAO.update(attendance);
    }

    public void deleteAttendance(String attendanceId) {
        UserAccount currentUser = requireCurrentUser();
        Role role = currentUser.getRole();

        if (role != Role.ADMIN && role != Role.HR) {
            throw new SecurityException("Only Admin or HR can delete attendance records.");
        }

        attendanceDAO.delete(attendanceId);
    }

    public void validateAttendanceForCurrentUser(Attendance attendance) {
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance record is required.");
        }

        validateEmployeeId(attendance.getEmployeeId());

        if (attendance.getDate() == null) {
            throw new IllegalArgumentException("Attendance date is required.");
        }

        UserAccount currentUser = requireCurrentUser();
        Role role = currentUser.getRole();

        if (role == null) {
            throw new SecurityException("Current user has no assigned role.");
        }

        LocalDate today = LocalDate.now();

        if (isRestrictedRole(role)) {
            if (!attendance.getEmployeeId().equals(String.valueOf(currentUser.getEmployeeId()))) {
                throw new SecurityException("You can only submit or update your own attendance.");
            }

            if (!attendance.getDate().equals(today)) {
                throw new IllegalArgumentException(
                        "Employee, IT, and Finance users can only submit attendance for today's date."
                );
            }
        }

        if (isAdminOrHr(role)) {
            if (attendance.getDate().isAfter(today)) {
                throw new IllegalArgumentException(
                        "Admin and HR users can only use past dates or today's date. Future dates are not allowed."
                );
            }
        }

        validateTimeRange(attendance.getLogIn(), attendance.getLogOut());
    }

    public double computeDailyHours(Attendance record) {
        // Invalid records contribute zero hours to payroll (see isValidForPayroll).
        if (!isValidForPayroll(record)) {
            return 0.0;
        }

        return computeDailyHours(record.getLogIn(), record.getLogOut());
    }

    /**
     * Single source of truth for whether an attendance record is "Valid" and
     * therefore counts as worked hours in payroll. A record is valid when:
     *   1. it is complete (both time-in and time-out are present), and
     *   2. its attendance_status is not one that means no work was done
     *      (Absent / On Leave).
     *
     * A null status is treated as payable — legacy imported records carry no
     * status, and they must still count.
     */
    public boolean isValidForPayroll(Attendance record) {
        if (record == null) {
            return false;
        }

        if (record.getLogIn() == null || record.getLogOut() == null) {
            return false;
        }

        return isPayableStatus(record.getAttendanceStatusId());
    }

    private boolean isPayableStatus(Integer statusId) {
        return statusId == null
                || (statusId != STATUS_ABSENT && statusId != STATUS_ON_LEAVE);
    }

    public double computeDailyHours(LocalTime timeIn, LocalTime timeOut) {
        if (timeIn == null || timeOut == null) {
            return 0.0;
        }

        if (!timeOut.isAfter(timeIn)) {
            return 0.0;
        }

        double minutes = Duration.between(timeIn, timeOut).toMinutes();
        return round(minutes / 60.0);
    }

    public double computeDailyHoursWithBreaks(LocalTime timeIn,
                                              LocalTime breakOut,
                                              LocalTime breakIn,
                                              LocalTime timeOut) {
        if (timeIn == null || breakOut == null || breakIn == null || timeOut == null) {
            return 0.0;
        }

        if (!breakOut.isAfter(timeIn)) {
            return 0.0;
        }

        if (!breakIn.isAfter(breakOut)) {
            return 0.0;
        }

        if (!timeOut.isAfter(breakIn)) {
            return 0.0;
        }

        double beforeBreak = Duration.between(timeIn, breakOut).toMinutes();
        double afterBreak = Duration.between(breakIn, timeOut).toMinutes();

        return round((beforeBreak + afterBreak) / 60.0);
    }

    public double computeTotalHours(String employeeId) {
        validateEmployeeId(employeeId);

        List<Attendance> records = attendanceDAO.findByEmployeeId(employeeId);
        double total = 0;

        for (Attendance record : records) {
            total += computeDailyHours(record);
        }

        return round(total);
    }

    public double computeTotalHours(String employeeId, LocalDate periodStart, LocalDate periodEnd) {
        validateEmployeeId(employeeId);

        if (periodStart == null || periodEnd == null) {
            throw new IllegalArgumentException("Period start and end dates are required.");
        }

        if (periodEnd.isBefore(periodStart)) {
            throw new IllegalArgumentException("Period end date cannot be before period start date.");
        }

        List<Attendance> records = attendanceDAO.findByEmployeeId(employeeId);
        double total = 0;

        for (Attendance record : records) {
            LocalDate date = record.getDate();

            if (date != null && !date.isBefore(periodStart) && !date.isAfter(periodEnd)) {
                total += computeDailyHours(record);
            }
        }

        return round(total);
    }

    public List<Attendance> getAllAttendance() {
        UserAccount currentUser = requireCurrentUser();
        Role role = currentUser.getRole();

        if (canViewRoleAttendance(role)) {
            return attendanceDAO.findAll();
        }

        return attendanceDAO.findByEmployeeId(String.valueOf(currentUser.getEmployeeId()));
    }

    public List<Attendance> getAllAttendance(String employeeId) {
        validateEmployeeId(employeeId);

        UserAccount currentUser = requireCurrentUser();
        Role role = currentUser.getRole();

        if (canViewRoleAttendance(role)) {
            return attendanceDAO.findByEmployeeId(employeeId);
        }

        if (!employeeId.equals(String.valueOf(currentUser.getEmployeeId()))) {
            throw new SecurityException("You can only view your own attendance.");
        }

        return attendanceDAO.findByEmployeeId(employeeId);
    }

    public Attendance findById(String attendanceId) {
        if (attendanceId == null || attendanceId.isBlank()) {
            throw new IllegalArgumentException("Attendance ID is required.");
        }

        Attendance attendance = attendanceDAO.findById(attendanceId);

        if (attendance == null) {
            return null;
        }

        UserAccount currentUser = requireCurrentUser();
        Role role = currentUser.getRole();

        if (canViewRoleAttendance(role)) {
            return attendance;
        }

        if (!attendance.getEmployeeId().equals(String.valueOf(currentUser.getEmployeeId()))) {
            throw new SecurityException("You can only view your own attendance.");
        }

        return attendance;
    }

    private void validateTimeRange(LocalTime timeIn, LocalTime timeOut) {
        if (timeIn == null && timeOut == null) {
            return;
        }

        if (timeIn == null) {
            throw new IllegalArgumentException("Time in is required.");
        }

        if (timeOut != null && !timeOut.isAfter(timeIn)) {
            throw new IllegalArgumentException("Time out must be after time in.");
        }
    }

    private UserAccount requireCurrentUser() {
        UserAccount currentUser = Session.getCurrentUser();

        if (currentUser == null) {
            throw new SecurityException("No active session found.");
        }

        return currentUser;
    }

    private void validateEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException("Employee ID is required.");
        }

        try {
            Integer.parseInt(employeeId);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Employee ID must be numeric.");
        }
    }

    private boolean isRestrictedRole(Role role) {
        return role == Role.EMPLOYEE
                || role == Role.IT
                || role == Role.FINANCE;
    }

    private boolean isAdminOrHr(Role role) {
        return role == Role.ADMIN || role == Role.HR;
    }

    private boolean canViewRoleAttendance(Role role) {
        return isAdminOrHr(role) || role == Role.FINANCE || role == Role.IT;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
