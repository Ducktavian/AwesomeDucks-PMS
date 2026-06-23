package com.motorph.model;

import java.time.LocalDate;

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
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }

    public String getSSSNumber() { return SSSNumber; }
    public String getPhilhealthNumber() { return philhealthNumber; }
    public String getTIN() { return TIN; }
    public String getPagIbigNumber() { return pagIbigNumber; }

    public String getStatus() { return status; }
    public String getPosition() { return position; }
    public String getImmediateSupervisor() { return immediateSupervisor; }

    public double getBasicSalary() { return basicSalary; }
    public double getRiceSubsidy() { return riceSubsidy; }
    public double getPhoneAllowance() { return phoneAllowance; }
    public double getClothingAllowance() { return clothingAllowance; }

    public Integer getPositionId() { return positionId; }
    public Integer getImmediateSupervisorId() { return immediateSupervisorId; }
    public Integer getEmploymentStatusId() { return employmentStatusId; }

    public void setPositionId(Integer positionId) { this.positionId = positionId; }
    public void setImmediateSupervisorId(Integer immediateSupervisorId) { this.immediateSupervisorId = immediateSupervisorId; }
    public void setEmploymentStatusId(Integer employmentStatusId) { this.employmentStatusId = employmentStatusId; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public double getHourlyRate() {
        if (hourlyRate > 0) return hourlyRate;
        double raw = basicSalary / (21 * 8);
        return Math.round(raw * 100.0) / 100.0;
    }

    public double getDailyRate() { return getHourlyRate() * 8; }
    public double getSemiMonthlyRate() { return basicSalary / 2; }

    public double getTotalAllowances() {
        return riceSubsidy + clothingAllowance + phoneAllowance;
    }

    @Override
    public String toString() {
        return "Employee{id='" + employeeId + "', name='" + getFullName()
                + "', position='" + position + "', status='" + status + "'}";
    }
}
