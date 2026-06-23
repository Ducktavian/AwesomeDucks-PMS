package com.motorph.model;

public class Payroll {

    private int payrollId;
    private int employeeId;
    private int payPeriodId;
    private int payrollStatusId;
    private double basicSalary;
    private double hourlyRate;
    private double hoursWorked;
    private double basicPay;
    private double overtimePay;
    private double grossPay;
    private double totalBenefits;
    private double totalDeductions;
    private double netPay;

    public Payroll(int employeeId, int payPeriodId, int payrollStatusId,
                   double basicSalary, double hourlyRate, double hoursWorked,
                   double basicPay, double overtimePay, double grossPay,
                   double totalBenefits, double totalDeductions, double netPay) {
        this.employeeId = employeeId;
        this.payPeriodId = payPeriodId;
        this.payrollStatusId = payrollStatusId;
        this.basicSalary = basicSalary;
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
        this.basicPay = basicPay;
        this.overtimePay = overtimePay;
        this.grossPay = grossPay;
        this.totalBenefits = totalBenefits;
        this.totalDeductions = totalDeductions;
        this.netPay = netPay;
    }

    public int getPayrollId() { return payrollId; }
    public void setPayrollId(int payrollId) { this.payrollId = payrollId; }

    public int getEmployeeId() { return employeeId; }
    public int getPayPeriodId() { return payPeriodId; }
    public int getPayrollStatusId() { return payrollStatusId; }
    public double getBasicSalary() { return basicSalary; }
    public double getHourlyRate() { return hourlyRate; }
    public double getHoursWorked() { return hoursWorked; }
    public double getBasicPay() { return basicPay; }
    public double getOvertimePay() { return overtimePay; }
    public double getGrossPay() { return grossPay; }
    public double getTotalBenefits() { return totalBenefits; }
    public double getTotalDeductions() { return totalDeductions; }
    public double getNetPay() { return netPay; }
}
