package com.motorph.model;

import java.time.LocalDate;

public class Employee {

    protected String employeeId;
    protected String lastName;
    protected String firstName;
    protected LocalDate birthday;
    protected String gender;
    protected String address;
    protected String phoneNumber;
    protected String email;

    protected String SSSNumber;
    protected String philhealthNumber;
    protected String TIN;
    protected String pagIbigNumber;

    protected String status;
    protected String department;
    protected String position;
    protected String immediateSupervisor;

    protected double basicSalary;
    protected double riceSubsidy;
    protected double phoneAllowance;
    protected double clothingAllowance;
    protected double hourlyRate;

    protected Integer positionId;
    protected Integer immediateSupervisorId;
    protected Integer employmentStatusId;

    public Employee() {}

    public Employee(
            String employeeId,
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
            double hourlyRate,
            String email) {

        this.employeeId = employeeId;
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
        this.hourlyRate = hourlyRate;
        this.email = email;
    }

    public String getEmployeeId() { return employeeId; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public LocalDate getBirthday() { return birthday; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }

    public String getSSSNumber() { return SSSNumber; }
    public String getPhilhealthNumber() { return philhealthNumber; }
    public String getTIN() { return TIN; }
    public String getPagIbigNumber() { return pagIbigNumber; }

    // Alias for UI compatibility
    public String getPagibigNumber() { return pagIbigNumber; }

    public String getStatus() { return status; }
    public String getDepartment() { return department; }
    public String getPosition() { return position; }
    public String getImmediateSupervisor() { return immediateSupervisor; }

    public double getBasicSalary() { return basicSalary; }
    public double getRiceSubsidy() { return riceSubsidy; }
    public double getPhoneAllowance() { return phoneAllowance; }
    public double getClothingAllowance() { return clothingAllowance; }

    public Integer getPositionId() { return positionId; }
    public Integer getImmediateSupervisorId() { return immediateSupervisorId; }
    public Integer getEmploymentStatusId() { return employmentStatusId; }

    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
    public void setGender(String gender) { this.gender = gender; }
    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }

    public void setSSSNumber(String SSSNumber) { this.SSSNumber = SSSNumber; }
    public void setPhilhealthNumber(String philhealthNumber) { this.philhealthNumber = philhealthNumber; }
    public void setTIN(String TIN) { this.TIN = TIN; }
    public void setPagIbigNumber(String pagIbigNumber) { this.pagIbigNumber = pagIbigNumber; }

    public void setStatus(String status) { this.status = status; }
    public void setDepartment(String department) { this.department = department; }
    public void setPosition(String position) { this.position = position; }
    public void setImmediateSupervisor(String immediateSupervisor) { this.immediateSupervisor = immediateSupervisor; }

    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    public void setRiceSubsidy(double riceSubsidy) { this.riceSubsidy = riceSubsidy; }
    public void setPhoneAllowance(double phoneAllowance) { this.phoneAllowance = phoneAllowance; }
    public void setClothingAllowance(double clothingAllowance) { this.clothingAllowance = clothingAllowance; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public void setPositionId(Integer positionId) { this.positionId = positionId; }
    public void setImmediateSupervisorId(Integer immediateSupervisorId) { this.immediateSupervisorId = immediateSupervisorId; }
    public void setEmploymentStatusId(Integer employmentStatusId) { this.employmentStatusId = employmentStatusId; }

    public String getFullName() {
        return safe(firstName) + " " + safe(lastName);
    }

    public double getHourlyRate() {
        if (hourlyRate > 0) return hourlyRate;
        double raw = basicSalary / (21 * 8);
        return Math.round(raw * 100.0) / 100.0;
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

    private String safe(String value) {
        return value == null ? "" : value;
    }

    @Override
    public String toString() {
        return "Employee{id='" + employeeId + "', name='" + getFullName()
                + "', position='" + position + "', status='" + status + "'}";
    }
}