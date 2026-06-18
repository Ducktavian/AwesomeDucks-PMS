package com.motorph.dao;

import com.motorph.model.Employee;
import com.motorph.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of EmployeeDAO.
 *
 * save()   — inserts into: employee → address → employee_address
 *                          → employee_government_id (x4) → compensation
 * update() — updates the same tables in place (compensation adds a new row
 *            to preserve salary history)
 * delete() — soft-delete: sets user_account.is_active = FALSE
 * findBy() — finds one employee by employee_id
 * findAll()— returns all employees with full profile
 */
public class JdbcEmployeeDAO implements EmployeeDAO {

    // ------------------------------------------------------------------
    // MUCH simpler because the database View already handles the joins, 
    // the MAX(CASE WHEN) aggregation, and the GROUP BY.
    // ------------------------------------------------------------------
    private static final String SELECT_FROM_VIEW = "SELECT * FROM v_full_employee_profile";
    // ------------------------------------------------------------------
    // findBy(employeeId) — returns one Employee or null if not found
    // ------------------------------------------------------------------
    @Override
    public Employee findBy(String employeeId) {
        // Simple append because the view handles all grouping constraints internally
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

    // ------------------------------------------------------------------
    // findAll() — returns every employee with full profile
    // ------------------------------------------------------------------
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

    // ------------------------------------------------------------------
    // save(Employee) — inserts a brand-new employee and all related rows.
    //
    // Order matters because of foreign keys:
    //   1. employee          (core record)
    //   2. address           (get generated address_id)
    //   3. employee_address  (link employee ↔ address)
    //   4. employee_government_id (4 rows: SSS, PhilHealth, TIN, Pag-IBIG)
    //   5. compensation      (salary record with today as effective_date)
    //
    // Everything runs inside one transaction — if any step fails,
    // all inserts are rolled back so you don't get orphaned rows.
    // ------------------------------------------------------------------
    @Override
    public void save(Employee employee) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // start transaction

            // 1. Insert into employee
            String insertEmployee = """
                    INSERT INTO employee
                        (employee_id, first_name, last_name, birthdate,
                         phone_number, email, position_id,
                         immediate_supervisor_id, employment_status_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement stmt = conn.prepareStatement(insertEmployee)) {
                stmt.setInt   (1, Integer.parseInt(employee.getEmployeeId()));
                stmt.setString(2, employee.getFirstName());
                stmt.setString(3, employee.getLastName());
                stmt.setDate  (4, Date.valueOf(employee.getBirthday()));
                stmt.setString(5, employee.getPhoneNumber());
                stmt.setString(6, employee.getEmail());
                stmt.setInt   (7, employee.getPositionId());           // must be resolved before calling save()
                stmt.setObject(8, employee.getSupervisorId() != null   // nullable
                        ? Integer.parseInt(employee.getSupervisorId()) : null, Types.INTEGER);
                stmt.setInt   (9, employee.getEmploymentStatusId());
                stmt.executeUpdate();
            }

            // 2. Insert address and get generated address_id
            int addressId;
            String insertAddress = """
                    INSERT INTO address (full_address) VALUES (?)
                    """;
            try (PreparedStatement stmt = conn.prepareStatement(
                    insertAddress, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, employee.getAddress());
                stmt.executeUpdate();
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    keys.next();
                    addressId = keys.getInt(1);
                }
            }

            // 3. Link employee ↔ address
            String insertEmployeeAddress = """
                    INSERT INTO employee_address (employee_id, address_id, address_type)
                    VALUES (?, ?, 'current')
                    """;
            try (PreparedStatement stmt = conn.prepareStatement(insertEmployeeAddress)) {
                stmt.setInt(1, Integer.parseInt(employee.getEmployeeId()));
                stmt.setInt(2, addressId);
                stmt.executeUpdate();
            }

            // 4. Insert government IDs
            //    government_id_type_id: 1=SSS, 2=PhilHealth, 3=TIN, 4=Pag-IBIG
            String insertGovId = """
                    INSERT INTO employee_government_id
                        (employee_id, government_id_type_id, id_number)
                    VALUES (?, ?, ?)
                    """;
            int empId = Integer.parseInt(employee.getEmployeeId());
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
            String insertComp = """
                    INSERT INTO compensation
                        (employee_id, basic_salary, rice_subsidy, phone_allowance,
                         clothing_allowance, gross_semi_monthly_rate, hourly_rate, effective_date)
                    VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE())
                    """;
            try (PreparedStatement stmt = conn.prepareStatement(insertComp)) {
                stmt.setInt   (1, empId);
                stmt.setDouble (2, employee.getBasicSalary());
                stmt.setDouble (3, employee.getRiceSubsidy());
                stmt.setDouble (4, employee.getPhoneAllowance());
                stmt.setDouble (5, employee.getClothingAllowance());
                stmt.setDouble (6, employee.getBasicSalary() / 2); // gross semi-monthly = half of basic
                stmt.setDouble (7, employee.getHourlyRate());
                stmt.executeUpdate();
            }

            conn.commit(); // all good — persist everything

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    // ------------------------------------------------------------------
    // update(Employee) — updates an existing employee's records.
    //
    // Compensation is NOT edited in place — a new row is inserted with
    // today as effective_date. This preserves salary history, and the
    // findAll/findBy queries always pick the latest row automatically.
    // ------------------------------------------------------------------
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
                        first_name             = ?,
                        last_name              = ?,
                        birthdate              = ?,
                        phone_number           = ?,
                        email                  = ?,
                        position_id            = ?,
                        immediate_supervisor_id = ?,
                        employment_status_id   = ?
                    WHERE employee_id = ?
                    """;
            try (PreparedStatement stmt = conn.prepareStatement(updateEmployee)) {
                stmt.setString(1, employee.getFirstName());
                stmt.setString(2, employee.getLastName());
                stmt.setDate  (3, Date.valueOf(employee.getBirthday()));
                stmt.setString(4, employee.getPhoneNumber());
                stmt.setString(5, employee.getEmail());
                stmt.setInt   (6, employee.getPositionId());
                stmt.setObject(7, employee.getSupervisorId() != null
                        ? Integer.parseInt(employee.getSupervisorId()) : null, Types.INTEGER);
                stmt.setInt   (8, employee.getEmploymentStatusId());
                stmt.setInt   (9, empId);
                stmt.executeUpdate();
            }

            // 2. Update current address
            //    Find the existing current address_id for this employee, then update it.
            String findAddressId = """
                    SELECT a.address_id FROM address a
                    JOIN employee_address ea ON a.address_id = ea.address_id
                    WHERE ea.employee_id = ? AND ea.address_type = 'current'
                    """;
            try (PreparedStatement stmt = conn.prepareStatement(findAddressId)) {
                stmt.setInt(1, empId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int addressId = rs.getInt("address_id");
                        String updateAddress = "UPDATE address SET full_address = ? WHERE address_id = ?";
                        try (PreparedStatement upd = conn.prepareStatement(updateAddress)) {
                            upd.setString(1, employee.getAddress());
                            upd.setInt   (2, addressId);
                            upd.executeUpdate();
                        }
                    }
                }
            }

            // 3. Update government IDs (INSERT ... ON DUPLICATE KEY UPDATE)
            //    Safe: won't create duplicates if the row already exists.
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
                        stmt.setInt   (1, empId);
                        stmt.setInt   (2, Integer.parseInt(entry[0]));
                        stmt.setString(3, entry[1]);
                        stmt.executeUpdate();
                    }
                }
            }

            // 4. Add new compensation row (preserves salary history)
            String insertComp = """
                    INSERT INTO compensation
                        (employee_id, basic_salary, rice_subsidy, phone_allowance,
                         clothing_allowance, gross_semi_monthly_rate, hourly_rate, effective_date)
                    VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE())
                    """;
            try (PreparedStatement stmt = conn.prepareStatement(insertComp)) {
                stmt.setInt   (1, empId);
                stmt.setDouble (2, employee.getBasicSalary());
                stmt.setDouble (3, employee.getRiceSubsidy());
                stmt.setDouble (4, employee.getPhoneAllowance());
                stmt.setDouble (5, employee.getClothingAllowance());
                stmt.setDouble (6, employee.getBasicSalary() / 2);
                stmt.setDouble (7, employee.getHourlyRate());
                stmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    // ------------------------------------------------------------------
    // delete(employeeId) — soft delete via user_account.is_active = FALSE.
    // The employee record stays intact; they just can't log in anymore.
    // ------------------------------------------------------------------
    @Override
    public void delete(String employeeId) {
        String sql = """
                UPDATE user_account SET is_active = FALSE
                WHERE employee_id = ?
                """;

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

    // ------------------------------------------------------------------
    // findById() and save/update(Object) — required by DAO interface,
    // delegate to the typed methods above.
    // ------------------------------------------------------------------
    @Override
    public Object findById(String id) {
        return findBy(id);
    }

    @Override
    public void save(Object entity) {
        save((Employee) entity);
    }

    @Override
    public void update(Object entity) {
        update((Employee) entity);
    }

    // ------------------------------------------------------------------
    // mapRow — converts one ResultSet row into an Employee object.
    // Kept private and reused by both findBy() and findAll().
    // ------------------------------------------------------------------
    private Employee mapRow(ResultSet rs) throws SQLException {
        // Adjust these strings to match the column names defined in your CREATE VIEW statement
        return new Employee(
                rs.getString("employee_id"),
                rs.getString("last_name"),
                rs.getString("first_name"),
                rs.getDate("birthdate").toLocalDate(),
                rs.getString("current_address"), // matched from view column alias
                rs.getString("phone_number"),
                rs.getString("sss_number"),
                rs.getString("philhealth_number"),
                rs.getString("tin_number"),
                rs.getString("pagibig_number"),
                rs.getString("employment_status"),
                rs.getString("position_name"),
                rs.getString("supervisor_name"), // If concatenated in your view, or keep individual
                rs.getFloat("basic_salary"),
                rs.getFloat("rice_subsidy"),
                rs.getFloat("phone_allowance"),
                rs.getFloat("clothing_allowance"),
                rs.getString("email")
        );
    }
}