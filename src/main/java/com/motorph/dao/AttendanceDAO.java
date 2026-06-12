package com.motorph.dao;

import com.motorph.model.Attendance;
import java.util.List;

/**
 *
 * @author Ducktavian
 */
public interface AttendanceDAO extends BaseDAO {
    Attendance findById(String attendanceId);
    List<Attendance> findByEmployeeId(String employeeId);
    List<Attendance> findAll();
    void save(Attendance attendance);
    void update(Attendance attendance);
    void delete(String attendanceId);
}
