package Test;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */


import com.motorph.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeCrudTest {

    private FakeEmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new FakeEmployeeService();
    }

    @Test
    void testCreateEmployee_validData() {
        Employee employee = employee("10001", "Santos", "Juan");

        employeeService.createEmployee(employee);

        Employee saved = employeeService.readEmployee("10001");

        assertNotNull(saved);
        assertEquals("10001", saved.getEmployeeId());
        assertEquals("Juan", saved.getFirstName());
        assertEquals("Santos", saved.getLastName());
    }

    @Test
    void testCreateEmployee_duplicatedID() {
        Employee firstEmployee = employee("10001", "Santos", "Juan");
        Employee duplicateEmployee = employee("10001", "Reyes", "Maria");

        employeeService.createEmployee(firstEmployee);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.createEmployee(duplicateEmployee)
        );

        assertEquals("Employee ID already exists.", exception.getMessage());
    }

    @Test
    void testReadEmployee_validID() {
        Employee employee = employee("10002", "Reyes", "Maria");

        employeeService.createEmployee(employee);

        Employee found = employeeService.readEmployee("10002");

        assertNotNull(found);
        assertEquals("Maria", found.getFirstName());
        assertEquals("Reyes", found.getLastName());
    }

    @Test
    void testReadEmployee_invalidID() {
        Employee found = employeeService.readEmployee("99999");

        assertNull(found);
    }

    @Test
    void testUpdateEmployee_validUpdate() {
        Employee employee = employee("10003", "Cruz", "Pedro");
        employeeService.createEmployee(employee);

        Employee updatedEmployee = employee("10003", "Cruz", "Pedro Miguel");
        updatedEmployee.setPosition("HR Manager");
        updatedEmployee.setBasicSalary(50000.00);

        employeeService.updateEmployee(updatedEmployee);

        Employee result = employeeService.readEmployee("10003");

        assertNotNull(result);
        assertEquals("Pedro Miguel", result.getFirstName());
        assertEquals("HR Manager", result.getPosition());
        assertEquals(50000.00, result.getBasicSalary(), 0.01);
    }

    @Test
    void testUpdateEmployee_missingField() {
        Employee employee = employee("10004", "Garcia", "Ana");
        employeeService.createEmployee(employee);

        Employee invalidUpdate = employee("10004", "", "");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.updateEmployee(invalidUpdate)
        );

        assertEquals("Employee first name is required.", exception.getMessage());
    }

    @Test
    void testDeleteEmployee_existing() {
        Employee employee = employee("10005", "Lopez", "Carlo");
        employeeService.createEmployee(employee);

        boolean deleted = employeeService.deleteEmployee("10005");

        assertTrue(deleted);
        assertNull(employeeService.readEmployee("10005"));
    }

    @Test
    void testDeleteEmployee_nonExistent() {
        boolean deleted = employeeService.deleteEmployee("99999");

        assertFalse(deleted);
    }

    @Test
    void testGetAllEmployees() {
        employeeService.createEmployee(employee("10006", "Mendoza", "Liza"));
        employeeService.createEmployee(employee("10007", "Torres", "Mark"));

        List<Employee> employees = employeeService.getAllEmployees();

        assertEquals(2, employees.size());
    }

    private static Employee employee(String employeeId, String lastName, String firstName) {
        Employee employee = new Employee();

        employee.setEmployeeId(employeeId);
        employee.setLastName(lastName);
        employee.setFirstName(firstName);
        employee.setStatus("Regular");
        employee.setPosition("Employee");
        employee.setBasicSalary(33600.00);
        employee.setRiceSubsidy(1500.00);
        employee.setPhoneAllowance(1000.00);
        employee.setClothingAllowance(500.00);

        return employee;
    }

    private static class FakeEmployeeService {

        private final Map<String, Employee> employees = new LinkedHashMap<>();

        void createEmployee(Employee employee) {
            validateEmployee(employee);

            if (employees.containsKey(employee.getEmployeeId())) {
                throw new IllegalArgumentException("Employee ID already exists.");
            }

            employees.put(employee.getEmployeeId(), employee);
        }

        Employee readEmployee(String employeeId) {
            return employees.get(employeeId);
        }

        void updateEmployee(Employee employee) {
            validateEmployee(employee);

            if (!employees.containsKey(employee.getEmployeeId())) {
                throw new IllegalArgumentException("Employee does not exist.");
            }

            employees.put(employee.getEmployeeId(), employee);
        }

        boolean deleteEmployee(String employeeId) {
            return employees.remove(employeeId) != null;
        }

        List<Employee> getAllEmployees() {
            return new ArrayList<>(employees.values());
        }

        private void validateEmployee(Employee employee) {
            if (employee == null) {
                throw new IllegalArgumentException("Employee is required.");
            }

            if (isBlank(employee.getEmployeeId())) {
                throw new IllegalArgumentException("Employee ID is required.");
            }

            if (isBlank(employee.getFirstName())) {
                throw new IllegalArgumentException("Employee first name is required.");
            }

            if (isBlank(employee.getLastName())) {
                throw new IllegalArgumentException("Employee last name is required.");
            }
        }

        private boolean isBlank(String value) {
            return value == null || value.trim().isEmpty();
        }
    }
}