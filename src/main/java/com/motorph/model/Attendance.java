package com.motorph.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Attendance {

    private int        attendanceId;       // attendance.attendance_id (PK)
    private String     employeeId;         // attendance.employee_id
    private String     lastName;           // joined from employee
    private String     firstName;          // joined from employee
    private LocalDate  date;               // attendance.attendance_date
    private LocalTime  logIn;              // attendance.time_in
    private LocalTime  logOut;             // attendance.time_out  (null = open session)
    private Integer    attendanceStatusId; // attendance.attendance_status_id (nullable)

    // Full constructor — used by DAO when reading rows (JOIN brings in names).
    public Attendance(int attendanceId, String employeeId,
                      String lastName, String firstName,
                      LocalDate date, LocalTime logIn, LocalTime logOut,
                      Integer attendanceStatusId) {
        this.attendanceId       = attendanceId;
        this.employeeId         = employeeId;
        this.lastName           = lastName;
        this.firstName          = firstName;
        this.date               = date;
        this.logIn              = logIn;
        this.logOut             = logOut;
        this.attendanceStatusId = attendanceStatusId;
    }

    // Write constructor — used when recording a new time-in (no names needed for INSERT).
    public Attendance(String employeeId, LocalDate date, LocalTime logIn) {
        this.employeeId = employeeId;
        this.date       = date;
        this.logIn      = logIn;
    }

    // Getters
    public int        getAttendanceId()       { return attendanceId; }
    public String     getEmployeeId()         { return employeeId; }
    public String     getLastName()           { return lastName; }
    public String     getFirstName()          { return firstName; }
    public LocalDate  getDate()               { return date; }
    public LocalTime  getLogIn()              { return logIn; }
    public LocalTime  getLogOut()             { return logOut; }
    public Integer    getAttendanceStatusId() { return attendanceStatusId; }

    // Setters for mutable fields
    public void setLogOut(LocalTime logOut)                       { this.logOut = logOut; }
    public void setAttendanceStatusId(Integer attendanceStatusId) { this.attendanceStatusId = attendanceStatusId; }
}
