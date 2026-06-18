package com.motorph.model;

import java.time.LocalDate;

/**
 *
 * @author Ducktavian
 */
public class Employee {

    
    protected String employeeId;
    protected String lastName;
    protected String firstName;
    protected LocalDate birthday;
    protected String address;
    protected String phoneNumber;
    protected String email;
    
    protected String SSSNumber;
    protected String philhealthNumber;
    protected String TIN;
    protected String pagIbigNumber;
    
    protected String status;
    protected String position;
    protected String immediateSupervisor;

    protected double basicSalary;
    protected double riceSubsidy;
    protected double phoneAllowance;
    protected double clothingAllowance;
    
    // Added through database design
    protected Integer positionId;
    protected Integer immediateSupervisorId;
    protected Integer employmentStatusId;
    
    // Empty parameters constructor
    public Employee () {
        
    }
    
    // Parameterized constructor
    public Employee(
            String employeeNumber,
            String lastName,
            String firstName,
            LocalDate birthday,
            String address,
            String phoneNumber,
            String SSSNumber,
            String philhealthNumber,
            String TIN,
            String pagIbigNumber,
            String status,
            String position,
            String immediateSupervisor,
            double basicSalary,
            double riceSubsidy,
            double phoneAllowance,
            double clothingAllowance,
            String email
    ) {
        this.employeeId = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthday = birthday;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.SSSNumber = SSSNumber;
        this.philhealthNumber = philhealthNumber;
        this.TIN = TIN;
        this.pagIbigNumber = pagIbigNumber;
        this.status = status;
        this.position = position;
        this.immediateSupervisor = immediateSupervisor;
        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.email = email;
    }

    // getters
    public String getEmployeeId() { return employeeId; }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPosition() {
        return position;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getSSSNumber() {
        return SSSNumber;
    }

    public String getPhilhealthNumber() {
        return philhealthNumber;
    }

    public String getTIN() {
        return TIN;
    }

    public String getPagIbigNumber() {
        return pagIbigNumber;
    }

    public String getStatus() {
        return status;
    }

    public String getImmediateSupervisor() {
        return immediateSupervisor;
    }

    public double getRiceSubsidy() {
        return riceSubsidy;
    }

    public double getPhoneAllowance() {
        return phoneAllowance;
    }

    public double getClothingAllowance() {
        return clothingAllowance;
    }
    
    public String getEmail() {
        return email;
    }    

    
    public String getFullName() { 
        return firstName + " " + lastName; 
    }
    
    public double getHourlyRate() { 
        double rawRate = basicSalary / (21 * 8);
        return Math.round(rawRate * 100.0) / 100.0;
    }
    
    public double getDailyRate() {
        return getHourlyRate() * 8;
    }
    
    public double getSemiMonthlyRate() {
        return basicSalary / 2;
    }
    
    public double getTotalAllowances() {
        return riceSubsidy + clothingAllowance + phoneAllowance;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public Integer getImmediateSupervisorId() {
        return immediateSupervisorId;
    }

    public Integer getEmploymentStatusId() {
        return employmentStatusId;
    }
    
    
    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public void setImmediateSupervisorId(Integer immediateSupervisorId) {
        this.immediateSupervisorId = immediateSupervisorId;
    }

    public void setEmploymentStatusId(Integer employmentStatusId) {
        this.employmentStatusId = employmentStatusId;
    }

    
 
    
}
