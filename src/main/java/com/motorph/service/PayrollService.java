package com.motorph.service;

import com.motorph.dao.PayPeriodDAO;
import com.motorph.dao.PayrollDAO;
import com.motorph.dao.PayslipDAO;
import com.motorph.model.AllowanceBreakdown;
import com.motorph.model.DeductionBreakdown;
import com.motorph.model.Employee;
import com.motorph.model.FinancialSummary;
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

    /**
     * Computes a payslip in memory WITHOUT writing anything to the database.
     * Used by the "Generate" preview in the payroll form :D
     */
    public Payslip computePayslip(Employee employee, LocalDate periodStart, LocalDate periodEnd) {
        double basicSalary = rateService.computeBasicSalary(employee);
        double overtimePay = 0; // overtime not modeled yet
        double holidayPay = 0; // holiday pay not modeled anywhere yet 

        double cutoffHours = attendanceService.computeTotalHours(employee.getEmployeeId(), periodStart, periodEnd);
        double hourlyRate = rateService.computeHourlyRate(employee);
        double cutoffGross = round(cutoffHours * hourlyRate);

        AllowanceBreakdown allowanceBreakdown = computeAllowances(employee);
        double totalGross = round(cutoffGross + allowanceBreakdown.getTotal());

        DeductionBreakdown deductionBreakdown;
        if (isSecondCutoff(periodEnd)) {
            LocalDate monthStart = periodStart.withDayOfMonth(1);
            LocalDate monthEnd = periodStart.withDayOfMonth(periodStart.lengthOfMonth());
            double monthlyHours = attendanceService.computeTotalHours(employee.getEmployeeId(), monthStart, monthEnd);
            double monthlyGross = round(monthlyHours * hourlyRate);
            deductionBreakdown = computeMonthlyDeductions(employee, monthlyGross);
        } else {
            deductionBreakdown = computeMonthlyDeductions();
        }

        double netPay = round(totalGross - deductionBreakdown.getTotal());

        String payslipNumber = generatePayslipNumber(employee.getEmployeeId(), periodEnd);

        return new Payslip(
                payslipNumber,
                0, // not persisted yet
                employee.getEmployeeId(),
                employee.getFullName(),
                employee.getPosition(),
                periodStart,
                periodEnd,
                cutoffHours,
                hourlyRate,
                basicSalary,
                overtimePay,
                holidayPay,
                totalGross,
                allowanceBreakdown,
                deductionBreakdown,
                netPay);
    }

    /**
     * Computes and persists 
     */
    public Payslip processPayslip(Employee employee, LocalDate periodStart, LocalDate periodEnd) {
        Payslip payslip = computePayslip(employee, periodStart, periodEnd);
        return persistPayslip(employee, payslip);
    }

    private Payslip persistPayslip(Employee employee, Payslip payslip) {
        int employeeId = Integer.parseInt(employee.getEmployeeId());

        PayPeriod payPeriod = payPeriodDAO.findOrCreate(
                payslip.getPeriodStart(), payslip.getPeriodEnd(), payslip.getPeriodEnd());

        int payrollId = payrollDAO.findPayrollId(employeeId, payPeriod.getPayPeriodId());

        if (payrollId == -1) {
            // No payroll yet for this employee/period - save it. basicPay is the
            // cutoff gross (gross minus allowances), the same figure computePayslip
            // derived it from.
            double basicPay = round(payslip.getGrossPay() - payslip.getAllowances());

            Payroll payroll = new Payroll(
                    employeeId,
                    payPeriod.getPayPeriodId(),
                    STATUS_PAID,
                    payslip.getBasicSalary(),
                    payslip.getHourlyRate(),
                    payslip.getTotalHours(),
                    basicPay,
                    payslip.getOvertimePay(),
                    payslip.getGrossPay(),
                    payslip.getAllowances(),
                    payslip.getTotalDeductions(),
                    payslip.getNetPay());

            payrollId = payrollDAO.save(payroll,
                    payslip.getAllowanceBreakdown(), payslip.getDeductionBreakdown());
        }

        payslip.setPayrollId(payrollId);

        // Guard by payroll_id (the actual unique constraint) 
        if (payslipDAO.findByPayrollId(payrollId) == null) {
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

    // delete a generated payslip by its id.
    public void deletePayslip(String payslipId) {
        payslipDAO.delete(payslipId);
    }

    // Count of payroll runs not yet paid (Draft/Processing) - used by the finance dashboard.
    public int getPendingPayrollCount() {
        return payrollDAO.countPending();
    }

    // Revenue vs. expenses per pay period - used by the finance/admin dashboard charts.
    public FinancialSummary getFinancialSummary() {
        return payrollDAO.findFinancialSummary();
    }

    // Pay period that today falls within, or null if none is set up yet.
    public PayPeriod getCurrentPayPeriod() {
        return payPeriodDAO.findCurrent(LocalDate.now());
    }

    // Earliest pay period starting after today, or null if none is set up yet.
    public PayPeriod getUpcomingPayPeriod() {
        return payPeriodDAO.findUpcoming(LocalDate.now());
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