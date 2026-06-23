package com.motorph.dao;

import com.motorph.model.Attendance;
import java.util.List;

public interface AttendanceDAO extends BaseDAO {
    Attendance findById(String attendanceId);
    List<Attendance> findByEmployeeId(String employeeId);
    List<Attendance> findAll();
    void save(Attendance attendance);
    void update(Attendance attendance);
    void delete(String attendanceId);

    /**
     * Returns the open (not yet timed-out) attendance record for the employee
     * on today's date, or null if none exists.
     */
    Attendance findOpenSession(String employeeId);
}
