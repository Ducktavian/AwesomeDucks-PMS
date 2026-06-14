package com.motorph.service;

import com.motorph.dao.AttendanceDAO;
import com.motorph.dao.JdbcAttendanceDAO;
import com.motorph.model.Attendance;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;


public class AttendanceService {
    
    private AttendanceDAO attendanceDAO;
    
    // Constructor
    public AttendanceService(AttendanceDAO attendanceDAO ) {
        this.attendanceDAO = attendanceDAO;
    }
    
    // Computes hours worked for a single attendance record ( helper function)
    public double computeDailyHours(Attendance record) {
        Duration duration = Duration.between(
                record.getLogIn(),
                record.getLogOut()
        );
        
        double hours = duration.toMinutes() / 60.0;
        
        return Math.round(hours * 100.0) / 100.0; // 2 decimal places
    }
    
    // Computes total hours worked for an employee
    public double computeTotalHours(String employeeId) {
        List<Attendance> records = attendanceDAO.findByEmployeeId(employeeId);
        
        double total = 0;
        
        for (Attendance record : records) {
            total += computeDailyHours(record);
        }
        
        return Math.round(total * 100.0) / 100.0;
    }
    
    // Period computation
    public double computeTotalHours(String employeeNumber, LocalDate periodStart, LocalDate periodEnd) {
        
        List<Attendance> records = attendanceDAO.findByEmployeeId(employeeNumber);
        
        double total = 0;
        for (Attendance record: records) {
            LocalDate date = record.getDate();
            boolean withinPeriod = (date.isEqual(periodStart) || date.isAfter(periodStart)) &&
                                    (date.isEqual(periodEnd) || date.isBefore(periodEnd));
            
            if (withinPeriod) {
                total += computeDailyHours(record);
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }
    
    // Get all attendances of all employess
    public List<Attendance> getAllAttendance() {
        return attendanceDAO.findAll();
    }
    
    // Get all attendances of an employee
    public List<Attendance> getAllAttendance(String employeeId) {
        return attendanceDAO.findByEmployeeId(employeeId);
    }
    
    public void timeIn(String employeeNumber) {
        //attendanceDAO.timeIn(employeeNumber);
    }
    
    public void timeOut(String employeeNumber) {
        //attendanceDAO.timeOut(employeeNumber);
    }
    /*
    public boolean hasOpenSession(String employeeNumber) {
        Attendance attendanceRecord = attendanceDAO.getOpenSession(employeeNumber);
        boolean hasOpenSession = (attendanceRecord == null) ? false : true;
        return hasOpenSession;
    }
    */
}
