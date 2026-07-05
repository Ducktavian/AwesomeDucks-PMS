package com.motorph.model;

import java.time.LocalDate;

/**
 *
 * @author Ducktavian
 */
public class Payslip {

    private String payslipId;

    private int payrollId;

    // Employee Info
    private String employeeNumber;
    private String employeeName;
    private String position;

    // Payroll Period
    private LocalDate periodStart;
    private LocalDate periodEnd;

    // Earnings
    private double totalHours;
    private double hourlyRate;
    private double basicSalary;
    private double overtimePay;
    private double holidayPay;

    private double grossPay;
    private AllowanceBreakdown allowanceBreakdown;

    // Deductions
    private DeductionBreakdown deductionBreakdown;

    // Final Pay
    private double netPay;

    public Payslip(
                   String payslipId,
                   int payrollId,
                   String employeeNumber,
                   String employeeName,
                   String position,
                   LocalDate periodStart,
                   LocalDate periodEnd,
                   double totalHours,
                   double hourlyRate,
                   double basicSalary,
                   double overtimePay,
                   double holidayPay,
                   double grossPay,
                   AllowanceBreakdown allowanceBreakdown,
                   DeductionBreakdown deductionBreakdown,
                   double netPay) {
        this.payslipId = payslipId;
        this.payrollId = payrollId;
        this.employeeNumber = employeeNumber;
        this.employeeName = employeeName;
        this.position = position;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.totalHours = totalHours;
        this.hourlyRate = hourlyRate;
        this.basicSalary = basicSalary;
        this.overtimePay = overtimePay;
        this.holidayPay = holidayPay;
        this.grossPay = grossPay;
        this.allowanceBreakdown = allowanceBreakdown;
        this.deductionBreakdown = deductionBreakdown;
        this.netPay = netPay;
    }


    public String getPayslipId() {
        return payslipId;
    }

    public void setPayslipId(String payslipId) {
        this.payslipId = payslipId;
    }

    public int getPayrollId() {
        return payrollId;
    }

    public void setPayrollId(int payrollId) {
        this.payrollId = payrollId;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }
    public String getEmployeeName() {
        return employeeName;
    }
    public String getPosition() {
        return position;
    }
    public LocalDate getPeriodStart() {
        return periodStart;
    }
    public LocalDate getPeriodEnd() {
        return periodEnd;
    }
    public double getTotalHours() {
        return totalHours;
    }
    public double getHourlyRate() {
        return hourlyRate;
    }
    public double getBasicSalary() {
        return basicSalary;
    }
    public double getOvertimePay() {
        return overtimePay;
    }
    // Always 0 today: no holiday-pay computation exists in PayrollService yet.
    public double getHolidayPay() {
        return holidayPay;
    }

    public double getGrossPay() {
        return grossPay;
    }

    public AllowanceBreakdown getAllowanceBreakdown() {
        return allowanceBreakdown;
    }
    public double getAllowances() {
        return allowanceBreakdown.getTotal();
    }

    public DeductionBreakdown getDeductionBreakdown() {
        return deductionBreakdown;
    }
    public double getTotalDeductions() {
        return deductionBreakdown.getTotal();
    }
    public double getTaxableIncome() {
        return deductionBreakdown.getTaxableIncome();
    }

    public double getNetPay() {
        return netPay;
    }

    public PayrollPeriod getPayrollPeriod() {
        if (periodStart.getDayOfMonth() <= 15) {
            return PayrollPeriod.FIRST_PERIOD;
        } else {
            return PayrollPeriod.SECOND_PERIOD;
        }
    }
    
    @Override
    public String toString() {
        return "Payslip{" +
                "payslipId='" + payslipId + '\'' +
                ", payrollId=" + payrollId +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", position='" + position + '\'' +
                ", periodStart=" + periodStart +
                ", periodEnd=" + periodEnd +
                ", totalHours=" + totalHours +
                ", hourlyRate=" + hourlyRate +
                ", basicSalary=" + basicSalary +
                ", overtimePay=" + overtimePay +
                ", holidayPay=" + holidayPay +
                ", grossPay=" + grossPay +
                ", allowanceBreakdown=" + allowanceBreakdown +
                ", totalAllowances=" + getAllowances() +
                ", deductionBreakdown=" + deductionBreakdown +
                ", totalDeductions=" + getTotalDeductions() +
                ", netPay=" + netPay +
                ", payrollPeriod=" + getPayrollPeriod() +
                '}';
    }

}