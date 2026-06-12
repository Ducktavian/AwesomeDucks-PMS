package com.motorph.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceRecord {
    
    private String employeeNumber;
    private String lastName;
    private String firstName;
    private LocalDate date;
    private LocalTime logIn;
    private LocalTime logOut;

    public AttendanceRecord(String employeeNumber, String lastName, String firstName, LocalDate date, LocalTime logIn, LocalTime logOut) {
        this.employeeNumber = employeeNumber;
        this.date = date;
        this.logIn = logIn;
        this.logOut = logOut;
        this.lastName = lastName;
        this.firstName = firstName;
    }
    
    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }
    
    

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getLogIn() {
        return logIn;
    }

    public LocalTime getLogOut() {
        return logOut;
    }
    
    public void setLogOut(LocalTime logOut) {
        this.logOut = logOut;
    }
    
    
}
