/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package Test;

import com.motorph.exception.UnauthorizedException;
import com.motorph.model.Employee;
import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportGenerationTest {

    @TempDir
    Path tempDir;

    @Test
    void testGenerateMonthlyReport_validEmployee() {
        FakeReportService reportService = new FakeReportService();
        UserAccount financeUser = user(1, 10001, "finance", Role.FINANCE);
        Employee employee = employee("10001", "Santos", "Juan", 33600.00);

        ReportData report = reportService.generateMonthlyReport(
                employee,
                YearMonth.of(2026, 7),
                financeUser
        );

        assertNotNull(report);
        assertEquals("10001", report.getEmployeeId());
        assertEquals("Juan Santos", report.getEmployeeName());
        assertEquals("2026-07", report.getPeriod());
        assertTrue(report.getGrossPay() > 0);
        assertTrue(report.getNetPay() > 0);
    }

    @Test
    void testGenerateSummaryForAllEmployees() {
        FakeReportService reportService = new FakeReportService();
        UserAccount financeUser = user(2, 10002, "finance.summary", Role.FINANCE);

        List<Employee> employees = new ArrayList<>();
        employees.add(employee("10001", "Santos", "Juan", 33600.00));
        employees.add(employee("10002", "Reyes", "Maria", 42000.00));
        employees.add(employee("10003", "Cruz", "Pedro", 50000.00));

        List<ReportData> summary = reportService.generateSummaryForAllEmployees(
                employees,
                YearMonth.of(2026, 7),
                financeUser
        );

        assertEquals(3, summary.size());
        assertEquals("10001", summary.get(0).getEmployeeId());
        assertEquals("10002", summary.get(1).getEmployeeId());
        assertEquals("10003", summary.get(2).getEmployeeId());
    }

    @Test
    void testExportReportAsPDF() throws IOException {
        FakeReportService reportService = new FakeReportService();
        UserAccount financeUser = user(3, 10003, "finance.pdf", Role.FINANCE);

        List<Employee> employees = new ArrayList<>();
        employees.add(employee("10001", "Santos", "Juan", 33600.00));
        employees.add(employee("10002", "Reyes", "Maria", 42000.00));

        List<ReportData> summary = reportService.generateSummaryForAllEmployees(
                employees,
                YearMonth.of(2026, 7),
                financeUser
        );

        Path pdfPath = tempDir.resolve("monthly-report.pdf");

        reportService.exportReportAsPDF(summary, pdfPath, financeUser);

        assertTrue(Files.exists(pdfPath));
        assertTrue(Files.size(pdfPath) > 0);

        String content = Files.readString(pdfPath, StandardCharsets.UTF_8);
        assertTrue(content.startsWith("%PDF"));
        assertTrue(content.contains("Juan Santos"));
        assertTrue(content.contains("Maria Reyes"));
    }

    @Test
    void testExportReportWithEmptyData() throws IOException {
        FakeReportService reportService = new FakeReportService();
        UserAccount financeUser = user(4, 10004, "finance.empty", Role.FINANCE);

        Path pdfPath = tempDir.resolve("empty-report.pdf");

        reportService.exportReportAsPDF(new ArrayList<>(), pdfPath, financeUser);

        assertTrue(Files.exists(pdfPath));
        assertTrue(Files.size(pdfPath) > 0);

        String content = Files.readString(pdfPath, StandardCharsets.UTF_8);
        assertTrue(content.startsWith("%PDF"));
        assertTrue(content.contains("No report data available"));
    }

    @Test
    void testUnauthorizedReportAccess() {
        FakeReportService reportService = new FakeReportService();
        UserAccount employeeUser = user(5, 10005, "employee", Role.EMPLOYEE);
        Employee employee = employee("10005", "Lopez", "Carlo", 33600.00);

        assertThrows(
                UnauthorizedException.class,
                () -> reportService.generateMonthlyReport(
                        employee,
                        YearMonth.of(2026, 7),
                        employeeUser
                )
        );
    }

    private static Employee employee(
            String employeeId,
            String lastName,
            String firstName,
            double basicSalary
    ) {
        Employee employee = new Employee();

        employee.setEmployeeId(employeeId);
        employee.setLastName(lastName);
        employee.setFirstName(firstName);
        employee.setBasicSalary(basicSalary);
        employee.setPosition("Employee");
        employee.setStatus("Regular");
        employee.setRiceSubsidy(1500.00);
        employee.setPhoneAllowance(1000.00);
        employee.setClothingAllowance(500.00);

        return employee;
    }

    private static UserAccount user(
            int userId,
            int employeeId,
            String username,
            Role role
    ) {
        return new UserAccount(userId, employeeId, username, "dummy-hash", role, true);
    }

    private static class FakeReportService {

        ReportData generateMonthlyReport(
                Employee employee,
                YearMonth period,
                UserAccount currentUser
        ) {
            authorizeReportAccess(currentUser);

            if (employee == null) {
                throw new IllegalArgumentException("Employee is required.");
            }

            double grossPay = round((employee.getBasicSalary() / 2) + employee.getTotalAllowances());
            double deductions = round(grossPay * 0.10);
            double netPay = round(grossPay - deductions);

            return new ReportData(
                    employee.getEmployeeId(),
                    employee.getFullName().trim(),
                    period.toString(),
                    grossPay,
                    deductions,
                    netPay
            );
        }

        List<ReportData> generateSummaryForAllEmployees(
                List<Employee> employees,
                YearMonth period,
                UserAccount currentUser
        ) {
            authorizeReportAccess(currentUser);

            List<ReportData> reports = new ArrayList<>();

            for (Employee employee : employees) {
                reports.add(generateMonthlyReport(employee, period, currentUser));
            }

            return reports;
        }

        void exportReportAsPDF(
                List<ReportData> reports,
                Path outputPath,
                UserAccount currentUser
        ) throws IOException {
            authorizeReportAccess(currentUser);

            if (outputPath == null) {
                throw new IllegalArgumentException("Output path is required.");
            }

            StringBuilder pdfContent = new StringBuilder();

            pdfContent.append("%PDF-1.4\n");
            pdfContent.append("MotorPH Payroll Report\n");

            if (reports == null || reports.isEmpty()) {
                pdfContent.append("No report data available\n");
            } else {
                for (ReportData report : reports) {
                    pdfContent.append(report.getEmployeeId())
                            .append(" - ")
                            .append(report.getEmployeeName())
                            .append(" - Gross: ")
                            .append(report.getGrossPay())
                            .append(" - Net: ")
                            .append(report.getNetPay())
                            .append("\n");
                }
            }

            pdfContent.append("%%EOF");

            Files.writeString(outputPath, pdfContent.toString(), StandardCharsets.UTF_8);
        }

        private void authorizeReportAccess(UserAccount currentUser) {
            if (currentUser == null || currentUser.getRole() == null) {
                throw new UnauthorizedException("No active authorized user.");
            }

            if (currentUser.getRole() != Role.FINANCE
                    && currentUser.getRole() != Role.ADMIN) {
                throw new UnauthorizedException("Only Finance or Admin can generate reports.");
            }
        }

        private double round(double value) {
            return Math.round(value * 100.0) / 100.0;
        }
    }

    private static class ReportData {

        private final String employeeId;
        private final String employeeName;
        private final String period;
        private final double grossPay;
        private final double deductions;
        private final double netPay;

        ReportData(
                String employeeId,
                String employeeName,
                String period,
                double grossPay,
                double deductions,
                double netPay
        ) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.period = period;
            this.grossPay = grossPay;
            this.deductions = deductions;
            this.netPay = netPay;
        }

        String getEmployeeId() {
            return employeeId;
        }

        String getEmployeeName() {
            return employeeName;
        }

        String getPeriod() {
            return period;
        }

        double getGrossPay() {
            return grossPay;
        }

        double getDeductions() {
            return deductions;
        }

        double getNetPay() {
            return netPay;
        }
    }
}