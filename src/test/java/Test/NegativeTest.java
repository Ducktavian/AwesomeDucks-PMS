/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package Test;

import com.motorph.model.Employee;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class NegativeTest {

    @Test
    void testNullEmployeeInput() {
        FakeEmployeeValidator validator = new FakeEmployeeValidator();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateEmployee(null, LocalDate.now())
        );

        assertEquals("Employee is required.", exception.getMessage());
    }

    @Test
    void testNegativeSalaryThrowsException() {
        FakeEmployeeValidator validator = new FakeEmployeeValidator();
        Employee employee = validEmployee();

        employee.setBasicSalary(-1000.00);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateEmployee(employee, LocalDate.now())
        );

        assertEquals("Basic salary cannot be negative.", exception.getMessage());
    }

    @Test
    void testFutureJoinDateIsInvalid() {
        FakeEmployeeValidator validator = new FakeEmployeeValidator();
        Employee employee = validEmployee();

        LocalDate futureJoinDate = LocalDate.now().plusDays(1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateEmployee(employee, futureJoinDate)
        );

        assertEquals("Join date cannot be in the future.", exception.getMessage());
    }

    @Test
    void testPhoneNumberFormatInvalid() {
        FakeEmployeeValidator validator = new FakeEmployeeValidator();
        Employee employee = validEmployee();

        employee.setPhoneNumber("123ABC");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateEmployee(employee, LocalDate.now())
        );

        assertEquals("Phone number format is invalid.", exception.getMessage());
    }

    @Test
    void testTINFormat() {
        FakeEmployeeValidator validator = new FakeEmployeeValidator();
        Employee employee = validEmployee();

        employee.setTIN("12345");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateEmployee(employee, LocalDate.now())
        );

        assertEquals("TIN format is invalid.", exception.getMessage());

        employee.setTIN("123-456-789-000");

        assertDoesNotThrow(
                () -> validator.validateEmployee(employee, LocalDate.now())
        );
    }

    @Test
    void testPhilhealthFormat() {
        FakeEmployeeValidator validator = new FakeEmployeeValidator();
        Employee employee = validEmployee();

        employee.setPhilhealthNumber("ABC-123");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateEmployee(employee, LocalDate.now())
        );

        assertEquals("PhilHealth number format is invalid.", exception.getMessage());

        employee.setPhilhealthNumber("12-345678901-2");

        assertDoesNotThrow(
                () -> validator.validateEmployee(employee, LocalDate.now())
        );
    }

    @Test
    void testPagibigFormat() {
        FakeEmployeeValidator validator = new FakeEmployeeValidator();
        Employee employee = validEmployee();

        employee.setPagIbigNumber("999");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateEmployee(employee, LocalDate.now())
        );

        assertEquals("Pag-IBIG number format is invalid.", exception.getMessage());

        employee.setPagIbigNumber("1234-5678-9012");

        assertDoesNotThrow(
                () -> validator.validateEmployee(employee, LocalDate.now())
        );
    }

    private static Employee validEmployee() {
        Employee employee = new Employee();

        employee.setEmployeeId("10001");
        employee.setLastName("Santos");
        employee.setFirstName("Juan");
        employee.setPhoneNumber("09171234567");
        employee.setTIN("123-456-789-000");
        employee.setPhilhealthNumber("12-345678901-2");
        employee.setPagIbigNumber("1234-5678-9012");
        employee.setStatus("Regular");
        employee.setPosition("Employee");
        employee.setBasicSalary(33600.00);
        employee.setRiceSubsidy(1500.00);
        employee.setPhoneAllowance(1000.00);
        employee.setClothingAllowance(500.00);

        return employee;
    }

    private static class FakeEmployeeValidator {

        void validateEmployee(Employee employee, LocalDate joinDate) {
            if (employee == null) {
                throw new IllegalArgumentException("Employee is required.");
            }

            if (employee.getBasicSalary() < 0) {
                throw new IllegalArgumentException("Basic salary cannot be negative.");
            }

            if (joinDate != null && joinDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Join date cannot be in the future.");
            }

            if (!isValidPhoneNumber(employee.getPhoneNumber())) {
                throw new IllegalArgumentException("Phone number format is invalid.");
            }

            if (!isValidTIN(employee.getTIN())) {
                throw new IllegalArgumentException("TIN format is invalid.");
            }

            if (!isValidPhilHealth(employee.getPhilhealthNumber())) {
                throw new IllegalArgumentException("PhilHealth number format is invalid.");
            }

            if (!isValidPagIbig(employee.getPagIbigNumber())) {
                throw new IllegalArgumentException("Pag-IBIG number format is invalid.");
            }
        }

        private boolean isValidPhoneNumber(String phoneNumber) {
            return phoneNumber != null && phoneNumber.matches("^09\\d{9}$");
        }

        private boolean isValidTIN(String tin) {
            return tin != null && tin.matches("^\\d{3}-\\d{3}-\\d{3}-\\d{3}$");
        }

        private boolean isValidPhilHealth(String philHealth) {
            return philHealth != null && philHealth.matches("^\\d{2}-\\d{9}-\\d{1}$");
        }

        private boolean isValidPagIbig(String pagIbig) {
            return pagIbig != null && pagIbig.matches("^\\d{4}-\\d{4}-\\d{4}$");
        }
    }
}