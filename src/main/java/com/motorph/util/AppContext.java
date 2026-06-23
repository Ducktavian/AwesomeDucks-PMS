package com.motorph.util;

import com.motorph.dao.*;
import com.motorph.service.*;

public class AppContext {
    
    // --- DAOs ---
    private static final AttendanceDAO attendanceDAO = new JdbcAttendanceDAO();
    private static final EmployeeDAO employeeDAO = new JdbcEmployeeDAO();
    //private static final LeaveDAO leaveDAO = new CsvLeaveDAO();
    private static final PayslipDAO payslipDAO = new JdbcPayslipDAO();
    private static final PayPeriodDAO payPeriodDAO = new JdbcPayPeriodDAO();
    private static final PayrollDAO payrollDAO = new JdbcPayrollDAO();
    private static final UserAccountDAO userAccountDAO = new JdbcUserAccountDAO();
    private static final RequestDAO requestDAO = new JdbcRequestDAO();

    // --- Services ---
    private static final AttendanceService attendanceService =
            new AttendanceService(attendanceDAO);

    private static final EmployeeService employeeService =
            new EmployeeService(employeeDAO);

    /*
    private static final LeaveService leaveService =
            new LeaveService(leaveDAO);
            */
    private static final RateService rateService = new RateService();
    private static final DeductionService deductionService = new DeductionService();

    private static final PayrollService payrollService =
            new PayrollService(attendanceService, rateService, deductionService, payPeriodDAO, payrollDAO, payslipDAO);

    private static final AuthService authService =
            new AuthService(userAccountDAO);

    private static final UserService userService =
            new UserService(userAccountDAO);

    private static final RequestService requestService = new RequestService(requestDAO);

    
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

    //public static LeaveService getLeaveService() {
     //   return leaveService;
    //}

    public static AuthService getAuthService() {
        return authService;
    }

    public static UserService getUserService() {
        return userService;
    }

    public static RequestService getRequestService() {
        return requestService;
    }

}