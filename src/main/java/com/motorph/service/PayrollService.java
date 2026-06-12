package com.motorph.service;

import com.motorph.dao.PayslipDAO;
import com.motorph.exception.UnauthorizedException;
import com.motorph.model.AllowanceBreakdown;
import com.motorph.model.DeductionBreakdown;
import com.motorph.model.Employee;
import com.motorph.model.Payslip;
import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import com.motorph.util.Session;

import java.time.LocalDate;
import java.util.List;


public class PayrollService {
    
    private AttendanceService attendanceService;
    private RateService rateService;
    private DeductionService deductionService;
    private PayslipDAO payslipDAO;

    
    // Constructor
    public PayrollService(AttendanceService attendanceService, RateService rateService, DeductionService deductionService, PayslipDAO payslipDAO) {
        this.attendanceService = attendanceService;
        this.rateService = rateService;
        this.deductionService = deductionService;
        this.payslipDAO = payslipDAO;
    }
    
    // Security
    public void processPayroll() {
        UserAccount currentUser = Session.getCurrentUser();
        
        if (currentUser == null) {
            throw new UnauthorizedException("No active session");
        }
        
        // Use Enum Role
        if (currentUser.getRole() != Role.FINANCE && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only Finance can process payroll.");
        }
        
        System.out.println("Security check passed. Processing payroll");
    }
    
    
    
    // Generate a payslip for a given payroll period
    public Payslip generatePayslip(Employee employee, LocalDate periodStart, LocalDate periodEnd) {
        processPayroll();
        
        // get cutoffHours (Hours workded)
        double cutoffHours = attendanceService.computeTotalHours(employee.getEmployeeNumber(), periodStart, periodEnd);
        // get hourly rate
        double hourlyRate = rateService.computeHourlyRate(employee);
        // derive gross pay
        double cutoffGross = round(cutoffHours * hourlyRate);
        
        // get allowances
        AllowanceBreakdown allowanceBreakdown = computeAllowances(employee);
        double totalAllowances = allowanceBreakdown.getTotal();
        
        // get Total Gross
        double totalGross = round(cutoffGross + totalAllowances);
        
        
        
        // Handle Deductions
        DeductionBreakdown deductionBreakdown = null;
        double totalDeductions = 0;
        
        // If second cutoff: Apply deductions
        if (isSecondCutoff(periodEnd)) {
            // Compute MONTHLY gross for deductions
            LocalDate monthStart = periodStart.withDayOfMonth(1);
            LocalDate monthEnd = periodStart.withDayOfMonth(periodStart.lengthOfMonth());
            double monthlyHours = attendanceService.computeTotalHours(employee.getEmployeeNumber(), monthStart, monthEnd);
            double monthlyGross = round(monthlyHours * hourlyRate);
            
            deductionBreakdown = computeMonthlyDeductions(employee, monthlyGross);
            totalDeductions = deductionBreakdown.getTotal();
        } else {
            deductionBreakdown = computeMonthlyDeductions();
            totalDeductions = deductionBreakdown.getTotal();
        }
       
        // Compute Netpay
        double netPay = round(totalGross - totalDeductions);
        
        //Generate payslipId
        String payslipId = generatePayslipId(employee.getEmployeeNumber(), periodEnd);
                
        // Create Payslip object
        Payslip payslip = new Payslip(
                payslipId,
                employee.getEmployeeNumber(),
                employee.getFullName(),
                employee.getPosition(),
                periodStart,
                periodEnd,
                cutoffHours,
                hourlyRate,
                totalGross,
                allowanceBreakdown,
                deductionBreakdown,
                netPay
        );
        
        
        return payslip;
    }
    
    public void savePayslip(Payslip payslip) throws Exception {
        // Write to CSV
        payslipDAO.savePayslip(payslip);
        
    }
    
    
    public List<Payslip> findPayslipsByEmployee(String employeeNumber) {
        return payslipDAO.findPayslipsByEmployee(employeeNumber);
    }
    
    public Payslip findPayslipsById(String payslipId) {
        return payslipDAO.findPayslipById(payslipId);
    }
    
    public List<Payslip> getAllPayslips() {
        return payslipDAO.getAllPayslips();
    }
   
    
    // Divide allowances by 2 (semi monthly)
    public AllowanceBreakdown computeAllowances(Employee employee) {
        AllowanceBreakdown allowances = 
            new AllowanceBreakdown(
                round(employee.getRiceSubsidy() / 2),
                round(employee.getPhoneAllowance() / 2),
                round(employee.getClothingAllowance() / 2)
            );
        return allowances;
    }
    
    
    // If first period return all zero for Deductions
    public DeductionBreakdown computeMonthlyDeductions() {
        return new DeductionBreakdown(
                0, 0, 0, 0
        );
    }
    
    // Deductions are computed in monthly and deducted on the second cutoff
    public DeductionBreakdown computeMonthlyDeductions(Employee emp, double monthlyGross) {
        
        // Monthly contributions
        double monthlySSS = deductionService.calculateSSSContribution(monthlyGross);
        double monthlyPhilHealth = deductionService.calculatePhilHealthContribution(monthlyGross);
        double monthlyPagIbig = deductionService.calculatePagIbigContribution(monthlyGross);
        
        // Calculate monthlyTaxableIncome
        double monthlyTaxableIncome = monthlyGross - monthlySSS - monthlyPhilHealth - monthlyPagIbig;
        // Calculate tax
        double tax = deductionService.calculateTax(monthlyTaxableIncome);
        
        // Return DeductionBreakdown object
        return new DeductionBreakdown(
                round(monthlySSS),
                round(monthlyPhilHealth),
                round(monthlyPagIbig),
                round(tax)
        );
    }
    
    private String generatePayslipId(String employeeNumber, LocalDate periodEnd) {
        
        int year = periodEnd.getYear();
        int month = periodEnd.getMonthValue();
        
        int cutoff = isSecondCutoff(periodEnd) ? 2 : 1;
        
        return employeeNumber + "-" +
                year + "-" +
                String.format("%02d", month) +
                "-C" + cutoff;
    }
    
    
    // Helper
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
    
    private boolean isSecondCutoff(LocalDate periodEnd) {
        return periodEnd.getDayOfMonth() == periodEnd.lengthOfMonth();
    }
    
    
    
    
    
    // THIS WAS ONLY USED ONCE FOR TESTING: THIS GENERATES ALL THE PAYSLIPS FROM ALL THE AVAILABLE ATTENDANCE RECORDS
    public void generatePayrollHistory(List<Employee> employees, LocalDate from, LocalDate to) {
        LocalDate cursor = from.withDayOfMonth(1);
        
        while (!cursor.isAfter(to)) {
            
            LocalDate monthStart = cursor.withDayOfMonth(1);
            LocalDate monthEnd = cursor.withDayOfMonth(cursor.lengthOfMonth());
            
            // Cutoff 1: 1-15
            LocalDate c1Start = monthStart;
            LocalDate c1End = cursor.withDayOfMonth(15);
            
            // CutOff 2: 16-End
            LocalDate c2Start = cursor.withDayOfMonth(16);
            LocalDate c2End = monthEnd;
            
            for (Employee emp: employees) {
                
                // Generate only if employee has attendance
                if (attendanceService.computeTotalHours(emp.getEmployeeNumber(), c1Start, c1End) > 0) {
                    generatePayslip(emp, c1Start, c1End);
                }
                
                if (attendanceService.computeTotalHours(emp.getEmployeeNumber(), c2Start, c2End) > 0) {
                    generatePayslip(emp, c2Start, c2End);
                }
            }
            
            cursor = cursor.plusMonths(1);
        }
    }
}
