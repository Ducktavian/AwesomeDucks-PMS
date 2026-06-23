package com.motorph.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Attendance {

    private int attendanceId;
    private String employeeId;
    private String lastName;
    private String firstName;
    private LocalDate date;
    private LocalTime logIn;
    private LocalTime logOut;
    private Integer attendanceStatusId;

    public Attendance(int attendanceId, String employeeId,
                      String lastName, String firstName,
                      LocalDate date, LocalTime logIn, LocalTime logOut,
                      Integer attendanceStatusId) {
        this.attendanceId = attendanceId;
        this.employeeId = employeeId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.date = date;
        this.logIn = logIn;
        this.logOut = logOut;
        this.attendanceStatusId = attendanceStatusId;
    }

    public Attendance(String employeeId, LocalDate date, LocalTime logIn) {
        this.employeeId = employeeId;
        this.date = date;
        this.logIn = logIn;
    }

    public int getAttendanceId() { return attendanceId; }
    public String getEmployeeId() { return employeeId; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public LocalDate getDate() { return date; }
    public LocalTime getLogIn() { return logIn; }
    public LocalTime getLogOut() { return logOut; }
    public Integer getAttendanceStatusId() { return attendanceStatusId; }

    public void setLogOut(LocalTime logOut) { this.logOut = logOut; }
    public void setAttendanceStatusId(Integer attendanceStatusId) { this.attendanceStatusId = attendanceStatusId; }
}
