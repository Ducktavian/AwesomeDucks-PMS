package Test;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */



import com.motorph.model.Employee;
import com.motorph.service.DeductionService;
import com.motorph.service.RateService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Trisha Gayle
 */
class PayrollTest {

    private static final double MONEY_DELTA = 0.01;

    @Test
    void testCalculateGrossSalary() {
        Employee employee = createEmployee("10001", 33600.00, 0, 0, 0);
        RateService rateService = new RateService();

        double hourlyRate = rateService.computeHourlyRate(employee);
        double hoursWorked = 80.00;
        double grossPay = round(hourlyRate * hoursWorked);

        assertEquals(200.00, hourlyRate, MONEY_DELTA);
        assertEquals(16000.00, grossPay, MONEY_DELTA);
    }

    @Test
    void testCalculateNetSalary_withTaxDeductions() {
        DeductionService deductionService = new DeductionService();

        double grossPay = 50000.00;

        double sss = deductionService.calculateSSSContribution(grossPay);
        double philHealth = deductionService.calculatePhilHealthContribution(grossPay);
        double pagIbig = deductionService.calculatePagIbigContribution(grossPay);

        double taxableIncome = grossPay - sss - philHealth - pagIbig;
        double tax = deductionService.calculateTax(taxableIncome);

        double totalDeductions = sss + philHealth + pagIbig + tax;
        double netPay = round(grossPay - totalDeductions);

        assertTrue(sss > 0, "SSS contribution should be greater than zero.");
        assertEquals(750.00, philHealth, MONEY_DELTA);
        assertEquals(100.00, pagIbig, MONEY_DELTA);
        assertTrue(tax > 0, "Tax should be greater than zero.");
        assertTrue(netPay < grossPay, "Net pay should be lower than gross pay after deductions.");
    }

    @Test
    void testCalculateOvertimePay() {
        double hourlyRate = 200.00;
        double totalHoursWorked = 10.50;

        double regularHours = Math.min(totalHoursWorked, 8.00);
        double overtimeHours = Math.max(totalHoursWorked - 8.00, 0.00);

        double regularPay = regularHours * hourlyRate;
        double overtimePay = overtimeHours * (hourlyRate * 1.25);
        double totalPay = round(regularPay + overtimePay);

        assertEquals(8.00, regularHours, MONEY_DELTA);
        assertEquals(2.50, overtimeHours, MONEY_DELTA);
        assertEquals(625.00, overtimePay, MONEY_DELTA);
        assertEquals(2225.00, totalPay, MONEY_DELTA);
    }

    @Test
    void testCalculateWithNoAllowances() {
        Employee employee = createEmployee("10002", 33600.00, 0, 0, 0);
        RateService rateService = new RateService();

        double hourlyRate = rateService.computeHourlyRate(employee);
        double hoursWorked = 80.00;
        double grossPay = round((hourlyRate * hoursWorked) + employee.getTotalAllowances());

        assertEquals(0.00, employee.getTotalAllowances(), MONEY_DELTA);
        assertEquals(16000.00, grossPay, MONEY_DELTA);
    }

    @Test
    void testCalculateWithBonusesAndDeductions() {
        Employee employee = createEmployee("10003", 33600.00, 1500.00, 1000.00, 500.00);
        RateService rateService = new RateService();

        double hourlyRate = rateService.computeHourlyRate(employee);
        double hoursWorked = 80.00;
        double bonus = 2000.00;
        double otherDeduction = 500.00;

        double grossPay = round((hourlyRate * hoursWorked) + employee.getTotalAllowances() + bonus);
        double netPay = round(grossPay - otherDeduction);

        assertEquals(3000.00, employee.getTotalAllowances(), MONEY_DELTA);
        assertEquals(21000.00, grossPay, MONEY_DELTA);
        assertEquals(20500.00, netPay, MONEY_DELTA);
    }

    @Test
    void testPayrollRoundingErrors() {
        Employee employee = createEmployee("10004", 62670.00, 0, 0, 0);
        RateService rateService = new RateService();

        double hourlyRate = rateService.computeHourlyRate(employee);
        double hoursWorked = 8.15;
        double grossPay = round(hourlyRate * hoursWorked);

        assertEquals(373.04, hourlyRate, MONEY_DELTA);
        assertEquals(3040.28, grossPay, MONEY_DELTA);
    }

    private static Employee createEmployee(
            String employeeId,
            double basicSalary,
            double riceSubsidy,
            double phoneAllowance,
            double clothingAllowance
    ) {
        Employee employee = new Employee();

        employee.setEmployeeId(employeeId);
        employee.setFirstName("Test");
        employee.setLastName("Employee");
        employee.setBasicSalary(basicSalary);
        employee.setRiceSubsidy(riceSubsidy);
        employee.setPhoneAllowance(phoneAllowance);
        employee.setClothingAllowance(clothingAllowance);

        return employee;
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}