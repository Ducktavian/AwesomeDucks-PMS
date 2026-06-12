package com.motorph.util;

import com.motorph.dao.*;
import com.motorph.service.*;

public class AppContext {

    // --- DAOs ---
    private static final AttendanceDAO attendanceDAO = new CsvAttendanceDAO();
    private static final EmployeeDAO employeeDAO = new CsvEmployeeDAO();
    private static final LeaveDAO leaveDAO = new CsvLeaveDAO();
    private static final PayslipDAO payslipDAO = new CsvPayslipDAO();
    private static final UserAccountDAO userAccountDAO = new CsvUserAccountDAO();

    // --- Services ---
    private static final AttendanceService attendanceService =
            new AttendanceService(attendanceDAO);

    private static final EmployeeService employeeService =
            new EmployeeService(employeeDAO);

    private static final LeaveService leaveService =
            new LeaveService(leaveDAO);

    private static final RateService rateService = new RateService();
    private static final DeductionService deductionService = new DeductionService();

    private static final PayrollService payrollService =
            new PayrollService(attendanceService, rateService, deductionService, payslipDAO);

    private static final AuthService authService =
            new AuthService(userAccountDAO);

    private static final UserManagementService userManagementService =
            new UserManagementService(userAccountDAO);

    
    // ---Getters---
    public static PayrollService getPayrollService() {
        return payrollService;
    }

    public static EmployeeService getEmployeeService() {
        return employeeService;
    }

    public static AttendanceService getAttendanceService() {
        return attendanceService;
    }

    public static LeaveService getLeaveService() {
        return leaveService;
    }

    public static AuthService getAuthService() {
        return authService;
    }

    public static UserManagementService getUserManagementService() {
        return userManagementService;
    }
}