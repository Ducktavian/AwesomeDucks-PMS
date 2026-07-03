package com.motorph.main;

import com.motorph.model.AllowanceBreakdown;
import com.motorph.model.DeductionBreakdown;
import com.motorph.model.Employee;
import com.motorph.model.Payslip;
import com.motorph.service.AttendanceService;
import com.motorph.service.EmployeeService;
import com.motorph.service.PayrollService;
import com.motorph.ui.login.Login;
import com.motorph.util.AppContext;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static com.motorph.util.Util.print;


public class Main {

    public static void main(String[] args) {
        
        // TESTING
        PayrollService payrollService = AppContext.getPayrollService();
        
        List<Payslip> payslips = payrollService.getAllPayslips();
        
        for (Payslip payslip: payslips) { 
            System.out.println(payslip);
        }
        
        EmployeeService empService = AppContext.getEmployeeService();
        List<Employee> employeeList = empService.getAllEmployees();
        
        try {
            LocalDate from = LocalDate.parse("2024-06-01");
            generatePayrollHistory(employeeList, from, LocalDate.now());
            exportPayrollSeedSQL(employeeList, from, LocalDate.now());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
                

        // ── TEST DATA: 8 dispute seed inserts (mirrors Help Center hardcoded sample) ─────
        // Run these SQL statements in MySQL Workbench before launching to populate test data.
        // Employee IDs: 10001=Admin, 10003=Finance, 10005=IT, 10007=HR, 10016=Employee
        /*
        INSERT INTO dispute (employee_id, dispute_type, reason, dispute_status, date_filed, category, target_field)
        VALUES (10005, 'INFORMATION_DISPUTE', 'Cannot access payroll portal', 'UNRESOLVED', '2026-03-15', 'System Access', 'portal_access');

        INSERT INTO dispute (employee_id, dispute_type, reason, dispute_status, date_filed, category, target_field)
        VALUES (10016, 'INFORMATION_DISPUTE', 'Password reset request', 'UNRESOLVED', '2026-03-14', 'Password Reset', 'login_password');

        INSERT INTO dispute (employee_id, dispute_type, reason, dispute_status, date_filed, category, target_field)
        VALUES (10007, 'INFORMATION_DISPUTE', 'Leave balance discrepancy', 'UNRESOLVED', '2026-03-13', 'Leave Balance', 'leave_balance');

        INSERT INTO dispute (employee_id, dispute_type, reason, dispute_status, reviewed_by_id, date_filed, date_reviewed, payslip_number)
        VALUES (10003, 'PAYROLL_DISPUTE', 'Payslip amount incorrect', 'RESOLVED', 10001, '2026-03-12', '2026-03-20', 'PS-2024-07-0003');

        INSERT INTO dispute (employee_id, dispute_type, reason, dispute_status, date_filed, category, target_field)
        VALUES (10005, 'INFORMATION_DISPUTE', 'VPN connection failure', 'UNRESOLVED', '2026-03-11', 'Email & VPN', 'vpn_access');

        INSERT INTO dispute (employee_id, dispute_type, reason, dispute_status, reviewed_by_id, date_filed, date_reviewed, category, target_field)
        VALUES (10016, 'INFORMATION_DISPUTE', 'Email account locked', 'RESOLVED', 10005, '2026-03-10', '2026-03-18', 'Email & VPN', 'email_account');

        INSERT INTO dispute (employee_id, dispute_type, reason, dispute_status, date_filed, category, target_field)
        VALUES (10016, 'INFORMATION_DISPUTE', 'Attendance record missing for March 5-7', 'UNRESOLVED', '2026-03-09', 'Attendance Record', 'attendance_log');

        INSERT INTO dispute (employee_id, dispute_type, reason, dispute_status, reviewed_by_id, date_filed, date_reviewed, category, target_field)
        VALUES (10007, 'INFORMATION_DISPUTE', 'System login timeout issue', 'RESOLVED', 10005, '2026-03-08', '2026-03-15', 'System Access', 'session_timeout');
        */
        // ─────────────────────────────────────────────────────────────────────────────────


        // Use invokeLater to ensure thread safety for Swing components
        java.awt.EventQueue.invokeLater(() -> {
            Login loginFrame = new Login(null);
            loginFrame.setLocationRelativeTo(null); // Centers the window on screen
            loginFrame.setVisible(true);
            
                  
        });
        
        
       
            
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    // FOR TESTING //
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void exportPayrollSeedSQL(List<Employee> employees, LocalDate from, LocalDate to) {
        AttendanceService attendanceService = AppContext.getAttendanceService();
        PayrollService payrollService = AppContext.getPayrollService();

        // Collect computed records and unique pay periods in insertion order.
        List<Object[]> records = new ArrayList<>();        // { Employee, Payslip, periodKey }
        LinkedHashMap<String, Integer> periodIds = new LinkedHashMap<>(); // key="start|end" -> sequential id
        int periodCounter = 1;

        LocalDate cursor = from.withDayOfMonth(1);
        while (!cursor.isAfter(to)) {
            LocalDate c1Start = cursor.withDayOfMonth(1);
            LocalDate c1End   = cursor.withDayOfMonth(15);
            LocalDate c2Start = cursor.withDayOfMonth(16);
            LocalDate c2End   = cursor.withDayOfMonth(cursor.lengthOfMonth());

            for (Employee emp : employees) {
                if (attendanceService.computeTotalHours(emp.getEmployeeId(), c1Start, c1End) > 0) {
                    Payslip p = payrollService.processPayslip(emp, c1Start, c1End);
                    if (p != null) {
                        String key = c1Start + "|" + c1End;
                        if (!periodIds.containsKey(key)) periodIds.put(key, periodCounter++);
                        records.add(new Object[]{ emp, p, key });
                    }
                }
                if (attendanceService.computeTotalHours(emp.getEmployeeId(), c2Start, c2End) > 0) {
                    Payslip p = payrollService.processPayslip(emp, c2Start, c2End);
                    if (p != null) {
                        String key = c2Start + "|" + c2End;
                        if (!periodIds.containsKey(key)) periodIds.put(key, periodCounter++);
                        records.add(new Object[]{ emp, p, key });
                    }
                }
            }
            cursor = cursor.plusMonths(1);
        }

        String outFile = "payroll_seed.sql";
        try (BufferedWriter w = new BufferedWriter(new FileWriter(outFile))) {
            w.write("-- Auto-generated payroll seed  |  period: " + from + " to " + to + "\n");
            w.write("-- Run this to replace pay_period/payroll/payslip/benefit/deduction seed rows.\n\n");

            // Disable FK checks so TRUNCATE can run in any order.
            w.write("SET FOREIGN_KEY_CHECKS = 0;\n");
            w.write("TRUNCATE TABLE `payslip`;\n");
            w.write("TRUNCATE TABLE `payroll_deduction`;\n");
            w.write("TRUNCATE TABLE `payroll_benefit`;\n");
            w.write("TRUNCATE TABLE `payroll`;\n");
            w.write("TRUNCATE TABLE `pay_period`;\n");
            w.write("SET FOREIGN_KEY_CHECKS = 1;\n\n");

            // --- pay_period ---
            w.write("-- Pay Periods\n");
            w.write("INSERT INTO `pay_period` (`pay_period_id`, `period_start_date`, `period_end_date`, `pay_date`, `payroll_status_id`, `created_at`, `created_by`) VALUES\n");
            List<Map.Entry<String, Integer>> periods = new ArrayList<>(periodIds.entrySet());
            for (int i = 0; i < periods.size(); i++) {
                Map.Entry<String, Integer> e = periods.get(i);
                String[] parts = e.getKey().split("\\|");
                LocalDate start   = LocalDate.parse(parts[0]);
                LocalDate end     = LocalDate.parse(parts[1]);
                LocalDate payDate = end.plusDays(5);
                w.write(String.format("(%d, '%s', '%s', '%s', 4, NOW(), NULL)%s%n",
                        e.getValue(), start, end, payDate,
                        i < periods.size() - 1 ? "," : ";"));
            }
            w.write("\n");

            // --- payroll ---
            w.write("-- Payroll Records\n");
            w.write("INSERT INTO `payroll` (`payroll_id`, `employee_id`, `pay_period_id`, `payroll_status_id`,"
                    + " `basic_salary`, `hourly_rate`, `hours_worked`, `basic_pay`, `overtime_pay`,"
                    + " `gross_pay`, `total_benefits`, `total_deductions`, `net_pay`, `created_at`, `created_by`) VALUES\n");
            for (int i = 0; i < records.size(); i++) {
                Employee emp = (Employee) records.get(i)[0];
                Payslip  p   = (Payslip)  records.get(i)[1];
                String   key = (String)   records.get(i)[2];
                int payrollId   = i + 1;
                int payPeriodId = periodIds.get(key);
                // basic_pay = gross_pay (DB) before allowances; gross_pay (Payslip DTO) already includes allowances.
                double basicPay = p.getGrossPay() - p.getAllowances();
                w.write(String.format("(%d, %s, %d, 4, %.2f, %.2f, %.2f, %.2f, 0.00, %.2f, %.2f, %.2f, %.2f, NOW(), NULL)%s%n",
                        payrollId,
                        emp.getEmployeeId(),
                        payPeriodId,
                        emp.getBasicSalary(),
                        p.getHourlyRate(),
                        p.getTotalHours(),
                        basicPay,
                        p.getGrossPay(),
                        p.getAllowances(),
                        p.getTotalDeductions(),
                        p.getNetPay(),
                        i < records.size() - 1 ? "," : ";"));
            }
            w.write("\n");

            // --- payslip ---
            w.write("-- Payslips\n");
            w.write("INSERT INTO `payslip` (`payslip_id`, `payroll_id`, `payslip_number`, `generated_at`, `created_at`, `created_by`) VALUES\n");
            for (int i = 0; i < records.size(); i++) {
                Payslip p = (Payslip) records.get(i)[1];
                int payrollId = i + 1;
                w.write(String.format("(%d, %d, '%s', NOW(), NOW(), NULL)%s%n",
                        payrollId, payrollId, p.getPayslipId(),
                        i < records.size() - 1 ? "," : ";"));
            }
            w.write("\n");

            // --- payroll_benefit (benefit_type_id: 1=Rice, 2=Phone, 3=Clothing) ---
            w.write("-- Payroll Benefits\n");
            w.write("INSERT INTO `payroll_benefit` (`payroll_id`, `benefit_type_id`, `amount`, `created_at`, `created_by`) VALUES\n");
            for (int i = 0; i < records.size(); i++) {
                Payslip p = (Payslip) records.get(i)[1];
                int payrollId = i + 1;
                AllowanceBreakdown ab = p.getAllowanceBreakdown();
                boolean lastRec = (i == records.size() - 1);
                w.write(String.format("(%d, 1, %.2f, NOW(), NULL),%n", payrollId, ab.getRiceSubsidy()));
                w.write(String.format("(%d, 2, %.2f, NOW(), NULL),%n", payrollId, ab.getPhoneAllowance()));
                w.write(String.format("(%d, 3, %.2f, NOW(), NULL)%s%n", payrollId, ab.getClothingAllowance(),
                        lastRec ? ";" : ","));
            }
            w.write("\n");

            // --- payroll_deduction (deduction_type_id: 1=SSS, 2=PhilHealth, 3=Pag-IBIG, 4=Tax) ---
            w.write("-- Payroll Deductions\n");
            w.write("INSERT INTO `payroll_deduction` (`payroll_id`, `deduction_type_id`, `amount`, `created_at`, `created_by`) VALUES\n");
            for (int i = 0; i < records.size(); i++) {
                Payslip p = (Payslip) records.get(i)[1];
                int payrollId = i + 1;
                DeductionBreakdown db = p.getDeductionBreakdown();
                boolean lastRec = (i == records.size() - 1);
                w.write(String.format("(%d, 1, %.2f, NOW(), NULL),%n", payrollId, db.getSss()));
                w.write(String.format("(%d, 2, %.2f, NOW(), NULL),%n", payrollId, db.getPhilHealth()));
                w.write(String.format("(%d, 3, %.2f, NOW(), NULL),%n", payrollId, db.getPagIbig()));
                w.write(String.format("(%d, 4, %.2f, NOW(), NULL)%s%n", payrollId, db.getWithholdingTax(),
                        lastRec ? ";" : ","));
            }

            System.out.printf("%nExported %d payroll records across %d pay periods → %s%n",
                    records.size(), periodIds.size(), outFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    // FOR TESTING //
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void generatePayrollHistory(List<Employee> employees, LocalDate from, LocalDate to) {
        AttendanceService attendanceService = AppContext.getAttendanceService();
        PayrollService payrollService = AppContext.getPayrollService();
        LocalDate cursor = from.withDayOfMonth(1);
        
        while (!cursor.isAfter(to)) {
            
            print("entered while looop");
            
            LocalDate monthStart = cursor.withDayOfMonth(1);
            LocalDate monthEnd = cursor.withDayOfMonth(cursor.lengthOfMonth());
            
            // Cutoff 1: 1-15
            LocalDate c1Start = monthStart;
            LocalDate c1End = cursor.withDayOfMonth(15);
            
            // CutOff 2: 16-End
            LocalDate c2Start = cursor.withDayOfMonth(16);
            LocalDate c2End = monthEnd;
            
            
            
            for (Employee emp: employees) {
                Payslip c1Payslip = null;
                Payslip c2Payslip = null;

                if (attendanceService.computeTotalHours(emp.getEmployeeId(), c1Start, c1End) > 0) {
                    c1Payslip = payrollService.processPayslip(emp, c1Start, c1End);
                }

                if (attendanceService.computeTotalHours(emp.getEmployeeId(), c2Start, c2End) > 0) {
                    c2Payslip = payrollService.processPayslip(emp, c2Start, c2End);
                }

                if (c1Payslip != null) System.out.println(c1Payslip);
                if (c2Payslip != null) System.out.println(c2Payslip);
            }
            
            cursor = cursor.plusMonths(1);
            
        }
    }
}
