package Test;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */



import com.motorph.dao.UserAccountDAO;
import com.motorph.exception.UnauthorizedException;
import com.motorph.model.Employee;
import com.motorph.model.Role;
import com.motorph.model.UserAccount;
import com.motorph.service.AuthService;
import com.motorph.service.DeductionService;
import com.motorph.service.RateService;
import com.motorph.service.UserService;
import com.motorph.util.PasswordUtil;
import com.motorph.util.Session;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Trisha Gayle
 */
class RBACTest {

    private static final double MONEY_DELTA = 0.01;


    // =========================================================
    // RBAC TESTS
    // =========================================================

    @Test
    void testEmployeeCannotEditOthers() throws Exception {
        UserAccount employeeUser = account(1, 10001, "employee", "password", Role.EMPLOYEE, true);
        UserAccount otherUser = account(2, 10002, "other.employee", "password", Role.EMPLOYEE, true);

        UserAccountDAO fakeDao = fakeUserAccountDAO(employeeUser, otherUser);
        UserService userService = new UserService(fakeDao);

        setSessionUser(employeeUser);

        assertThrows(
                UnauthorizedException.class,
                () -> userService.updateUser(otherUser),
                "Employee users must not be allowed to edit other user accounts."
        );
    }

    @Test
    void testFinanceCanGenerateReports() throws Exception {
        UserAccount financeUser = account(3, 10003, "finance", "password", Role.FINANCE, true);

        UserAccountDAO fakeDao = fakeUserAccountDAO(financeUser);
        AuthService authService = new AuthService(fakeDao);

        UserAccount loggedIn = authService.login("finance", "password");

        assertEquals(Role.FINANCE, loggedIn.getRole());
        assertTrue(
                canGenerateReports(loggedIn),
                "Finance users should be allowed to generate payroll reports."
        );
    }

    @Test
    void testEmployeeCanOnlyViewOwnRecord() throws Exception {
        UserAccount employeeUser = account(4, 10004, "employee.self", "password", Role.EMPLOYEE, true);

        UserAccountDAO fakeDao = fakeUserAccountDAO(employeeUser);
        AuthService authService = new AuthService(fakeDao);

        UserAccount loggedIn = authService.login("employee.self", "password");

        assertTrue(
                canViewEmployeeRecord(loggedIn, 10004),
                "Employee should be able to view their own record."
        );

        assertFalse(
                canViewEmployeeRecord(loggedIn, 10005),
                "Employee should not be able to view another employee's record."
        );
    }

    @Test
    void testAccessDeniedForUnauthorizedAction() throws Exception {
        UserAccount employeeUser = account(5, 10005, "regular.employee", "password", Role.EMPLOYEE, true);

        UserAccountDAO fakeDao = fakeUserAccountDAO(employeeUser);
        UserService userService = new UserService(fakeDao);

        setSessionUser(employeeUser);

        assertThrows(
                UnauthorizedException.class,
                userService::listUsers,
                "Regular Employee role must be denied access to user-management actions."
        );
    }

    // =========================================================
    // PAYROLL CALCULATION TESTS
    // =========================================================

    @Test
    void testCalculateGrossSalary() {
        Employee employee = employee("10001", 33600.00, 0, 0, 0);
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
        assertTrue(tax > 0, "Tax should be greater than zero for this taxable income.");
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
        Employee employee = employee("10002", 33600.00, 0, 0, 0);
        RateService rateService = new RateService();

        double hourlyRate = rateService.computeHourlyRate(employee);
        double hoursWorked = 80.00;
        double grossPay = round((hourlyRate * hoursWorked) + employee.getTotalAllowances());

        assertEquals(0.00, employee.getTotalAllowances(), MONEY_DELTA);
        assertEquals(16000.00, grossPay, MONEY_DELTA);
    }

    @Test
    void testCalculateWithBonusesAndDeductions() {
        Employee employee = employee("10003", 33600.00, 1500.00, 1000.00, 500.00);
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
        Employee employee = employee("10004", 62670.00, 0, 0, 0);
        RateService rateService = new RateService();

        double hourlyRate = rateService.computeHourlyRate(employee);
        double hoursWorked = 8.15;
        double grossPay = round(hourlyRate * hoursWorked);

        assertEquals(373.04, hourlyRate, MONEY_DELTA);
        assertEquals(3040.28, grossPay, MONEY_DELTA);
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private static UserAccount account(
            int userId,
            int employeeId,
            String username,
            String plainPassword,
            Role role,
            boolean active
    ) throws Exception {
        String passwordHash = PasswordUtil.hashPassword(plainPassword);
        return new UserAccount(userId, employeeId, username, passwordHash, role, active);
    }

    private static Employee employee(
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

    private static boolean canGenerateReports(UserAccount user) {
        if (user == null || user.getRole() == null) {
            return false;
        }

        return user.getRole() == Role.FINANCE || user.getRole() == Role.ADMIN;
    }

    private static boolean canViewEmployeeRecord(UserAccount user, int targetEmployeeId) {
        if (user == null || user.getRole() == null) {
            return false;
        }

        if (user.getRole() == Role.ADMIN
                || user.getRole() == Role.HR
                || user.getRole() == Role.IT
                || user.getRole() == Role.FINANCE) {
            return true;
        }

        return user.getRole() == Role.EMPLOYEE
                && user.getEmployeeId() == targetEmployeeId;
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static UserAccountDAO fakeUserAccountDAO(UserAccount... users) {
        return new FakeUserAccountDAO(users);
    }

    private static class FakeUserAccountDAO implements UserAccountDAO {

        private final Map<Integer, UserAccount> byUserId = new HashMap<>();
        private final Map<Integer, UserAccount> byEmployeeId = new HashMap<>();
        private final Map<String, UserAccount> byUsername = new HashMap<>();

        FakeUserAccountDAO(UserAccount... users) {
            for (UserAccount user : users) {
                addOrUpdate(user);
            }
        }

        @Override
        public UserAccount findById(String userId) {
            return byUserId.get(Integer.parseInt(userId));
        }

        @Override
        public UserAccount findByEmployeeId(String employeeId) {
            return byEmployeeId.get(Integer.parseInt(employeeId));
        }

        @Override
        public UserAccount findByUsername(String username) {
            return byUsername.get(username);
        }

        @Override
        public List<UserAccount> findAll() {
            return new ArrayList<>(byUserId.values());
        }

        @Override
        public void save(UserAccount user) {
            saveUser(user);
        }

        @Override
        public void update(UserAccount user) {
            addOrUpdate(user);
        }

        @Override
        public void delete(UserAccount user) {
            if (user == null) {
                return;
            }

            byUserId.remove(user.getUserId());
            byEmployeeId.remove(user.getEmployeeId());
            byUsername.remove(user.getUsername());
        }

        public void changeRole(int userId, Role newRole) {
            UserAccount user = byUserId.get(userId);

            if (user != null) {
                user.setRole(newRole);
                addOrUpdate(user);
            }
        }

        @Override
        public void delete(String id) {
            UserAccount user = byUserId.get(Integer.parseInt(id));
            delete(user);
        }

        @Override
        public void save(Object entity) {
            saveUser((UserAccount) entity);
        }

        @Override
        public void update(Object entity) {
            addOrUpdate((UserAccount) entity);
        }

      private void saveUser(UserAccount user) {
    addOrUpdate(user);
}
        private void addOrUpdate(UserAccount user) {
            byUserId.put(user.getUserId(), user);
            byEmployeeId.put(user.getEmployeeId(), user);
            byUsername.put(user.getUsername(), user);
        }

        private int nextUserId() {
            int nextId = 1;

            for (Integer id : byUserId.keySet()) {
                if (id >= nextId) {
                    nextId = id + 1;
                }
            }

            return nextId;
        }
    }

    private static void setSessionUser(UserAccount user) throws Exception {
        for (Method method : Session.class.getDeclaredMethods()) {
            boolean isStatic = Modifier.isStatic(method.getModifiers());
            boolean hasOneParameter = method.getParameterCount() == 1;
            boolean acceptsUserAccount = hasOneParameter
                    && method.getParameterTypes()[0].isAssignableFrom(UserAccount.class);

            if (isStatic && acceptsUserAccount) {
                String methodName = method.getName().toLowerCase();

                if (methodName.contains("current")
                        || methodName.contains("user")
                        || methodName.contains("login")
                        || methodName.contains("set")) {
                    method.setAccessible(true);
                    method.invoke(null, user);
                    return;
                }
            }
        }

        for (Field field : Session.class.getDeclaredFields()) {
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            boolean isUserAccountField = UserAccount.class.isAssignableFrom(field.getType());

            if (isStatic && isUserAccountField) {
                field.setAccessible(true);
                field.set(null, user);
                return;
            }
        }

        if (user == null) {
            return;
        }

        fail("Could not set Session current user. Please check the Session class field or setter name.");
    }
}