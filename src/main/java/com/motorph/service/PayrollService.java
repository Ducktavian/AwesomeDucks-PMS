package com.motorph.service;

import com.motorph.dao.PayPeriodDAO;
import com.motorph.dao.PayrollDAO;
import com.motorph.dao.PayslipDAO;
import com.motorph.model.AllowanceBreakdown;
import com.motorph.model.DeductionBreakdown;
import com.motorph.model.Employee;
import com.motorph.model.PayPeriod;
import com.motorph.model.Payroll;
import com.motorph.model.Payslip;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Ducktavian
 */
public class PayrollService {

    // payroll_status_id = 4 ("Paid") - see seed data for `payroll_status`.
    private static final int STATUS_PAID = 4;

    private final AttendanceService attendanceService;
    private final RateService rateService;
    private final DeductionService deductionService;
    private final PayPeriodDAO payPeriodDAO;
    private final PayrollDAO payrollDAO;
    private final PayslipDAO payslipDAO;

    public PayrollService(AttendanceService attendanceService, RateService rateService,
                           DeductionService deductionService, PayPeriodDAO payPeriodDAO,
                           PayrollDAO payrollDAO, PayslipDAO payslipDAO) {
        this.attendanceService = attendanceService;
        this.rateService = rateService;
        this.deductionService = deductionService;
        this.payPeriodDAO = payPeriodDAO;
        this.payrollDAO = payrollDAO;
        this.payslipDAO = payslipDAO;
    }

    public Payslip processPayslip(Employee employee, LocalDate periodStart, LocalDate periodEnd) {
        int employeeId = Integer.parseInt(employee.getEmployeeId());

        PayPeriod payPeriod = payPeriodDAO.findOrCreate(periodStart, periodEnd, periodEnd);

        int payrollId = payrollDAO.findPayrollId(employeeId, payPeriod.getPayPeriodId());
        AllowanceBreakdown allowanceBreakdown;
        DeductionBreakdown deductionBreakdown;
        double cutoffHours;
        double hourlyRate;
        double totalGross;
        double netPay;

        if (payrollId == -1) {
            // No payroll yet for this employee/period - compute and save it.
            cutoffHours = attendanceService.computeTotalHours(employee.getEmployeeId(), periodStart, periodEnd);
            hourlyRate = rateService.computeHourlyRate(employee);
            double cutoffGross = round(cutoffHours * hourlyRate);

            allowanceBreakdown = computeAllowances(employee);
            totalGross = round(cutoffGross + allowanceBreakdown.getTotal());

            if (isSecondCutoff(periodEnd)) {
                LocalDate monthStart = periodStart.withDayOfMonth(1);
                LocalDate monthEnd = periodStart.withDayOfMonth(periodStart.lengthOfMonth());
                double monthlyHours = attendanceService.computeTotalHours(employee.getEmployeeId(), monthStart, monthEnd);
                double monthlyGross = round(monthlyHours * hourlyRate);
                deductionBreakdown = computeMonthlyDeductions(employee, monthlyGross);
            } else {
                deductionBreakdown = computeMonthlyDeductions();
            }

            netPay = round(totalGross - deductionBreakdown.getTotal());

            Payroll payroll = new Payroll(
                    employeeId,
                    payPeriod.getPayPeriodId(),
                    STATUS_PAID,
                    rateService.computeBasicSalary(employee),
                    hourlyRate,
                    cutoffHours,
                    cutoffGross,
                    0, // overtime not modeled yet - plug in when work_time_request integration is added
                    totalGross,
                    allowanceBreakdown.getTotal(),
                    deductionBreakdown.getTotal(),
                    netPay);

            payrollId = payrollDAO.save(payroll, allowanceBreakdown, deductionBreakdown);
        } else {
            // Payroll already exists — recompute figures for the Payslip DTO only,
            // no duplicate rows written.
            cutoffHours = attendanceService.computeTotalHours(employee.getEmployeeId(), periodStart, periodEnd);
            hourlyRate = rateService.computeHourlyRate(employee);
            allowanceBreakdown = computeAllowances(employee);
            totalGross = round(cutoffHours * hourlyRate + allowanceBreakdown.getTotal());

            if (isSecondCutoff(periodEnd)) {
                LocalDate monthStart = periodStart.withDayOfMonth(1);
                LocalDate monthEnd = periodStart.withDayOfMonth(periodStart.lengthOfMonth());
                double monthlyHours = attendanceService.computeTotalHours(employee.getEmployeeId(), monthStart, monthEnd);
                double monthlyGross = round(monthlyHours * hourlyRate);
                deductionBreakdown = computeMonthlyDeductions(employee, monthlyGross);
            } else {
                deductionBreakdown = computeMonthlyDeductions();
            }

            netPay = round(totalGross - deductionBreakdown.getTotal());
        }

        String payslipNumber = generatePayslipNumber(employee.getEmployeeId(), periodEnd);

        Payslip payslip = new Payslip(
                payslipNumber,
                payrollId,
                employee.getEmployeeId(),
                employee.getFullName(),
                employee.getPosition(),
                periodStart,
                periodEnd,
                cutoffHours,
                hourlyRate,
                totalGross,
                allowanceBreakdown,
                deductionBreakdown,
                netPay);

        // Only save if this payslip doesn't already exist (avoids duplicate key on re-runs).
        if (payslipDAO.findById(payslipNumber) == null) {
            payslipDAO.save(payslip);
        }
        return payslip;
    }

    public List<Payslip> findPayslipsByEmployee(String employeeNumber) {
        return payslipDAO.findByEmployeeId(employeeNumber);
    }

    public Payslip findPayslipById(String payslipId) {
        return payslipDAO.findById(payslipId);
    }

    public List<Payslip> getAllPayslips() {
        return payslipDAO.findAll();
    }

    // Divide allowances by 2 (semi-monthly)
    private AllowanceBreakdown computeAllowances(Employee employee) {
        return new AllowanceBreakdown(
                round(employee.getRiceSubsidy() / 2),
                round(employee.getPhoneAllowance() / 2),
                round(employee.getClothingAllowance() / 2)
        );
    }

    // First cutoff: deductions are not withheld yet.
    private DeductionBreakdown computeMonthlyDeductions() {
        return new DeductionBreakdown(0, 0, 0, 0);
    }

    // Second cutoff: deductions are computed on the full month's gross and withheld.
    private DeductionBreakdown computeMonthlyDeductions(Employee emp, double monthlyGross) {
        double monthlySSS = deductionService.calculateSSSContribution(monthlyGross);
        double monthlyPhilHealth = deductionService.calculatePhilHealthContribution(monthlyGross);
        double monthlyPagIbig = deductionService.calculatePagIbigContribution(monthlyGross);

        double monthlyTaxableIncome = monthlyGross - monthlySSS - monthlyPhilHealth - monthlyPagIbig;
        double tax = deductionService.calculateTax(monthlyTaxableIncome);

        return new DeductionBreakdown(
                round(monthlySSS),
                round(monthlyPhilHealth),
                round(monthlyPagIbig),
                round(tax));
    }

    private String generatePayslipNumber(String employeeNumber, LocalDate periodEnd) {
        int year = periodEnd.getYear();
        int month = periodEnd.getMonthValue();
        int cutoff = isSecondCutoff(periodEnd) ? 2 : 1;

        return employeeNumber + "-" + year + "-" + String.format("%02d", month) + "-C" + cutoff;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private boolean isSecondCutoff(LocalDate periodEnd) {
        return periodEnd.getDayOfMonth() == periodEnd.lengthOfMonth();
    }
}