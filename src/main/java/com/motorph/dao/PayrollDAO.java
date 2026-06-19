package com.motorph.dao;

import com.motorph.model.AllowanceBreakdown;
import com.motorph.model.DeductionBreakdown;
import com.motorph.model.Payroll;

/**
 *
 * @author Ducktavian
 */
public interface PayrollDAO {
    int save(Payroll payroll, AllowanceBreakdown allowances, DeductionBreakdown deductions);
    int findPayrollId(int employeeId, int payPeriodId);
}