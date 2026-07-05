package com.motorph.dao;

import com.motorph.model.Employee;
import com.motorph.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;


public class JdbcEmployeeDAO implements EmployeeDAO {

    // The view already handles all joins, pivots, and GROUP BY.
    private static final String SELECT_FROM_VIEW =
            "SELECT * FROM v_full_employee_profile";

    @Override
    public Map<String, Integer> findAllPositions() {
        return findLookupValues(
                "SELECT position_id, position_name FROM employee_position ORDER BY position_name",
                "position_name", "position_id");
    }

    @Override
    public Map<String, Integer> findAllEmploymentStatuses() {
        return findLookupValues(
                "SELECT employment_status_id, status_type FROM employment_status ORDER BY employment_status_id",
                "status_type", "employment_status_id");
    }

    @Override
    public Map<String, Integer> findAllDepartments() {
        return findLookupValues(
                "SELECT department_id, department_name FROM department ORDER BY department_name",
                "department_name", "department_id");
    }

    @Override
    public Map<String, Integer> findPositionsByDepartment(int departmentId) {
        Map<String, Integer> values = new LinkedHashMap<>();
        String sql = "SELECT position_id, position_name FROM employee_position "
                + "WHERE department_id = ? ORDER BY position_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    values.put(rs.getString("position_name"), rs.getInt("position_id"));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load positions for department " + departmentId, e);
        }
        return values;
    }

    private Map<String, Integer> findLookupValues(String sql, String nameColumn, String idColumn) {
        Map<String, Integer> values = new LinkedHashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                values.put(rs.getString(nameColumn), rs.getInt(idColumn));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load employee lookup values.", e);
        }
        return values;
    }

    @Override
    public Employee findBy(String employeeId) {
        String sql = SELECT_FROM_VIEW + " WHERE employee_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(employeeId));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Employee> findAll() {
        String sql = SELECT_FROM_VIEW + " ORDER BY employee_id ASC";
        List<Employee> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    

    @Override
    public void save(Employee employee) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int empId = Integer.parseInt(employee.getEmployeeId());

            // 1. Insert core employee row
            String insertEmployee = """
                    INSERT INTO employee
                        (employee_id, first_name, last_name, birthdate,
                         phone_number, email, position_id,
                         immediate_supervisor_id, employment_status_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement stmt = conn.prepareStatement(insertEmployee)) {
                stmt.setInt(1, empId);
                stmt.setString(2, employee.getFirstName());
                stmt.setString(3, employee.getLastName());
                stmt.setDate(4, Date.valueOf(employee.getBirthday()));
                stmt.setString(5, employee.getPhoneNumber());
                stmt.setString(6, employee.getEmail());
                stmt.setInt(7, employee.getPositionId());

                // immediateSupervisorId is nullable
                Integer supervisorId = employee.getImmediateSupervisorId();
                if (supervisorId != null) {
                    stmt.setInt(8, supervisorId);
                } else {
                    stmt.setNull(8, Types.INTEGER);
                }

                stmt.setInt(9, employee.getEmploymentStatusId());
                stmt.executeUpdate();
            }

            // 2. Insert address row; capture the generated address_id
            int addressId;
            String insertAddress = "INSERT INTO address (full_address) VALUES (?)";
            try (PreparedStatement stmt = conn.prepareStatement(
                    insertAddress, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, employee.getAddress());
                stmt.executeUpdate();
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    keys.next();
                    addressId = keys.getInt(1);
                }
            }

            // 3. Link employee â†” address
            String insertEmployeeAddress = """
                    INSERT INTO employee_address (employee_id, address_id, address_type)
                    VALUES (?, ?, 'current')
                    """;
            try (PreparedStatement stmt = conn.prepareStatement(insertEmployeeAddress)) {
                stmt.setInt(1, empId);
                stmt.setInt(2, addressId);
                stmt.executeUpdate();
            }

            // 4. Insert government IDs (skip any that are blank)
            //    type_id mapping: 1 = SSS, 2 = PhilHealth, 3 = TIN, 4 = Pag-IBIG
            String insertGovId = """
                    INSERT INTO employee_government_id
                        (employee_id, government_id_type_id, id_number)
                    VALUES (?, ?, ?)
                    """;
            String[][] govIds = {
                {"1", employee.getSSSNumber()},
                {"2", employee.getPhilhealthNumber()},
                {"3", employee.getTIN()},
                {"4", employee.getPagIbigNumber()}
            };
            try (PreparedStatement stmt = conn.prepareStatement(insertGovId)) {
                for (String[] entry : govIds) {
                    if (entry[1] != null && !entry[1].isBlank()) {
                        stmt.setInt   (1, empId);
                        stmt.setInt   (2, Integer.parseInt(entry[0]));
                        stmt.setString(3, entry[1]);
                        stmt.executeUpdate();
                    }
                }
            }

            // 5. Insert compensation (effective today)
            //    gross_semi_monthly_rate delegates to the model's own formula
            //    so the calculation is consistent everywhere.
            String insertComp = """
                    INSERT INTO compensation
                        (employee_id, basic_salary, rice_subsidy, phone_allowance,
                         clothing_allowance, gross_semi_monthly_rate, hourly_rate, effective_date)
                    VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE())
                    """;
            try (PreparedStatement stmt = conn.prepareStatement(insertComp)) {
                stmt.setInt(1, empId);
                stmt.setDouble(2, employee.getBasicSalary());
                stmt.setDouble(3, employee.getRiceSubsidy());
                stmt.setDouble(4, employee.getPhoneAllowance());
                stmt.setDouble(5, employee.getClothingAllowance());
                stmt.setDouble(6, employee.getSemiMonthlyRate()); // basicSalary / 2
                stmt.setDouble(7, employee.getHourlyRate());      // computed or stored
                stmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            rollback(conn);
            throw new IllegalStateException("Unable to save employee.", e);
        } finally {
            resetAndClose(conn);
        }
    }

    @Override
    public void update(Employee employee) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int empId = Integer.parseInt(employee.getEmployeeId());

            // 1. Update core employee fields
            String updateEmployee = """
                    UPDATE employee SET
                        first_name = ?,
                        last_name = ?,
                        birthdate = ?,
                        phone_number = ?,
                        email = ?,
                        position_id = ?,
                        immediate_supervisor_id = ?,
                        employment_status_id = ?
                    WHERE employee_id = ?
                    """;
            try (PreparedStatement stmt = conn.prepareStatement(updateEmployee)) {
                stmt.setString(1, employee.getFirstName());
                stmt.setString(2, employee.getLastName());
                stmt.setDate  (3, Date.valueOf(employee.getBirthday()));
                stmt.setString(4, employee.getPhoneNumber());
                stmt.setString(5, employee.getEmail());
                stmt.setInt   (6, employee.getPositionId());

                Integer supervisorId = employee.getImmediateSupervisorId();
                if (supervisorId != null) {
                    stmt.setInt(7, supervisorId);
                } else {
                    stmt.setNull(7, Types.INTEGER);
                }

                stmt.setInt(8, employee.getEmploymentStatusId());
                stmt.setInt(9, empId);
                stmt.executeUpdate();
            }

            // 2. Update the employee's current address text
            String findAddressId = """
                    SELECT a.address_id
                    FROM address a
                    JOIN employee_address ea ON a.address_id = ea.address_id
                    WHERE ea.employee_id = ? AND ea.address_type = 'current'
                    """;
            try (PreparedStatement stmt = conn.prepareStatement(findAddressId)) {
                stmt.setInt(1, empId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int addressId = rs.getInt("address_id");
                        String updateAddress =
                                "UPDATE address SET full_address = ? WHERE address_id = ?";
                        try (PreparedStatement upd = conn.prepareStatement(updateAddress)) {
                            upd.setString(1, employee.getAddress());
                            upd.setInt   (2, addressId);
                            upd.executeUpdate();
                        }
                    }
                }
            }

            // 3. Upsert government IDs
            //    ON DUPLICATE KEY UPDATE is safe: won't create a duplicate if
            //    the row already exists, just updates the id_number value.
            String upsertGovId = """
                    INSERT INTO employee_government_id
                        (employee_id, government_id_type_id, id_number)
                    VALUES (?, ?, ?)
                    ON DUPLICATE KEY UPDATE id_number = VALUES(id_number)
                    """;
            String[][] govIds = {
                {"1", employee.getSSSNumber()},
                {"2", employee.getPhilhealthNumber()},
                {"3", employee.getTIN()},
                {"4", employee.getPagIbigNumber()}
            };
            try (PreparedStatement stmt = conn.prepareStatement(upsertGovId)) {
                for (String[] entry : govIds) {
                    if (entry[1] != null && !entry[1].isBlank()) {
                        stmt.setInt(1, empId);
                        stmt.setInt(2, Integer.parseInt(entry[0]));
                        stmt.setString(3, entry[1]);
                        stmt.executeUpdate();
                    }
                }
            }

            // 4. Insert a new compensation row to preserve salary history
            String insertComp = """
                    INSERT INTO compensation
                        (employee_id, basic_salary, rice_subsidy, phone_allowance,
                         clothing_allowance, gross_semi_monthly_rate, hourly_rate, effective_date)
                    VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE())
                    """;
            try (PreparedStatement stmt = conn.prepareStatement(insertComp)) {
                stmt.setInt(1, empId);
                stmt.setDouble(2, employee.getBasicSalary());
                stmt.setDouble(3, employee.getRiceSubsidy());
                stmt.setDouble(4, employee.getPhoneAllowance());
                stmt.setDouble(5, employee.getClothingAllowance());
                stmt.setDouble(6, employee.getSemiMonthlyRate());
                stmt.setDouble(7, employee.getHourlyRate());
                stmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            rollback(conn);
            e.printStackTrace();
        } finally {
            resetAndClose(conn);
        }
    }

    // The employee row stays intact; only the login is disabled.

    @Override
    public void delete(String employeeId) {
        String sql = "UPDATE user_account SET is_active = FALSE WHERE employee_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(employeeId));
            int rows = stmt.executeUpdate();

            if (rows == 0) {
                System.out.println("No user_account found for employee_id: " + employeeId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

  
    @Override
    public Object findById(String id) { return findBy(id); }

    @Override
    public void save(Object entity)   { save((Employee) entity); }

    @Override
    public void update(Object entity) { update((Employee) entity); }

   
    private Employee mapRow(ResultSet rs) throws SQLException {
        Employee emp = new Employee(
                rs.getString("employee_id"),
                rs.getString("last_name"),
                rs.getString("first_name"),
                rs.getDate("birthdate").toLocalDate(),
                rs.getString("current_address"),     // view alias for full_address
                rs.getString("phone_number"),
                rs.getString("sss_number"),           // pivoted gov ID
                rs.getString("philhealth_number"),    // pivoted gov ID
                rs.getString("tin_number"),            // pivoted gov ID
                rs.getString("pagibig_number"),        // pivoted gov ID
                rs.getString("employment_status"),    // view alias for status_type
                rs.getString("position_name"),
                rs.getString("supervisor_name"),      // CONCAT in the view
                rs.getDouble("basic_salary"),
                rs.getDouble("rice_subsidy"),
                rs.getDouble("phone_allowance"),
                rs.getDouble("clothing_allowance"),
                rs.getDouble("hourly_rate"),           // read from compensation table
                rs.getString("email")
        );

        // Populate the raw FK IDs so callers can use them for further writes
        // without an extra query.  getInt() returns 0 for SQL NULL; wasNull()
        // lets us turn that back into a Java null for the nullable supervisor.
        emp.setDepartment(rs.getString("department_name")); // from the view's dept join
        emp.setPositionId(rs.getInt("position_id")); // never null (DB constraint)

        int supervisorId = rs.getInt("immediate_supervisor_id");
        emp.setImmediateSupervisorId(rs.wasNull() ? null : supervisorId);

        emp.setEmploymentStatusId(rs.getInt("employment_status_id")); // never null

        return emp;
    }

    
    private void rollback(Connection conn) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    private void resetAndClose(Connection conn) {
        if (conn != null) {
            try { conn.setAutoCommit(true); conn.close(); }
            catch (SQLException ex) { ex.printStackTrace(); }
        }
    }



}
