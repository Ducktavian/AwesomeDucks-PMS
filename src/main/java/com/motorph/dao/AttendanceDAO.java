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

    Attendance findOpenSession(String employeeId);

    // Uses v_attendance_summary — returns total hours for a specific month.
    double computeMonthlyHours(String employeeId, int year, int month);
}
