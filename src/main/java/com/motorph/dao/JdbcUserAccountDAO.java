package com.motorph.dao;

import com.motorph.config.DatabaseConnection;
import com.motorph.model.Role;
import com.motorph.model.UserAccount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class JdbcUserAccountDAO implements UserAccountDAO {


    // SQL constants
    private static final String SELECT_ALL =
            "SELECT user_account_id, employee_id, username, password_hash, " +
            "       is_active, role_id, role_name " +
            "FROM v_user_account";

    private static final String SELECT_BY_ID =
            SELECT_ALL + " WHERE user_account_id = ?";

    private static final String SELECT_BY_EMPLOYEE_ID =
            SELECT_ALL + " WHERE employee_id = ?";

    private static final String SELECT_BY_USERNAME =
            SELECT_ALL + " WHERE username = ?";

    private static final String INSERT_USER =
            "INSERT INTO user_account (employee_id, username, password_hash, is_active) " +
            "VALUES (?, ?, ?, ?)";

    private static final String UPDATE_USER =
            "UPDATE user_account " +
            "SET username = ?, password_hash = ?, is_active = ? " +
            "WHERE user_account_id = ?";

    private static final String DELETE_BY_ID =
            "DELETE FROM user_account WHERE user_account_id = ?";

    
    // Interface methods
    @Override
    public UserAccount findById(String userId) {
        try {
            return findByIntId(Integer.parseInt(userId));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("userId must be a numeric string, got: " + userId, e);
        }
    }

    // Convenience overload — preferred when you already have an int.
    public UserAccount findById(int userId) {
        return findByIntId(userId);
    }

    @Override
    public UserAccount findByEmployeeId(String employeeId) {
        try {
            return findByEmployeeIntId(Integer.parseInt(employeeId));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("employeeId must be a numeric string, got: " + employeeId, e);
        }
    }

    /** Convenience overload — preferred when you already have an int. */
    public UserAccount findByEmployeeId(int employeeId) {
        return findByEmployeeIntId(employeeId);
    }

    
    //Look up a user by their login username.

    public UserAccount findByUsername(String username) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_USERNAME)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByUsername failed for: " + username, e);
        }
        return null;
    }

    @Override
    public List<UserAccount> findAll() {
        List<UserAccount> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findAll failed", e);
        }
        return list;
    }

    /**
     * Insert a new user_account row.
     * The auto-generated user_account_id is written back into the object.
     * Role assignment must be done separately via account_role.
     */
    @Override
    public void save(UserAccount user) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_USER,
                     Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt    (1, user.getEmployeeId());
            ps.setString (2, user.getUsername());
            ps.setString (3, user.getPasswordHash());
            ps.setBoolean(4, user.isActive());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    // NEW: write the generated PK back so callers (e.g. UserService.createUser)
                    // can immediately assign the account's role via account_role.
                    user.setUserId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("save(UserAccount) failed for username: " + user.getUsername(), e);
        }
    }

    // Generic Object overload required by the interface — delegates to typed version. 
    @Override
    public void save(Object entity) {
        if (entity instanceof UserAccount) {
            save((UserAccount) entity);
        } else {
            throw new IllegalArgumentException("save(Object) expects a UserAccount, got: "
                    + entity.getClass().getName());
        }
    }

    
     // Update username, password_hash, and is_active for an existing account.
     // Identified by userId (user_account_id).
    @Override
    public void update(UserAccount user) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_USER)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setBoolean(3, user.isActive());
            ps.setInt(4, user.getUserId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("update(UserAccount) failed for userId: " + user.getUserId(), e);
        }
    }

    /** Generic Object overload — delegates to typed version. */
    @Override
    public void update(Object entity) {
        if (entity instanceof UserAccount) {
            update((UserAccount) entity);
        } else {
            throw new IllegalArgumentException("update(Object) expects a UserAccount, got: "
                    + entity.getClass().getName());
        }
    }

    
    // Delete by UserAccount object (uses its userId).
     
    @Override
    public void delete(UserAccount user) {
        delete(String.valueOf(user.getUserId()));
    }

    
    // Delete by user_account_id (passed as a String per the interface contract).
    
    @Override
    public void delete(String id) {
        try {
            int userId = Integer.parseInt(id);
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(DELETE_BY_ID)) {

                ps.setInt(1, userId);
                ps.executeUpdate();
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("delete: id must be numeric, got: " + id, e);
        } catch (SQLException e) {
            throw new RuntimeException("delete failed for id: " + id, e);
        }
    }
    
    @Override
    public void changeRole(int userId, Role newRole) {
        String deleteSql = "DELETE FROM account_role WHERE user_account_id = ?";
        String insertSql = """
                INSERT INTO account_role (user_account_id, role_id, created_at)
                VALUES (?, ?, NOW())
                """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement deletePs = conn.prepareStatement(deleteSql);
                 PreparedStatement insertPs = conn.prepareStatement(insertSql)) {

                deletePs.setInt(1, userId);
                deletePs.executeUpdate();

                insertPs.setInt(1, userId);
                insertPs.setInt(2, newRole.getRoleId());
                insertPs.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to change role for userId=" + userId, e);
        }
    }

    
    // Private helpers    
    private UserAccount findByIntId(int userId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById failed for id=" + userId, e);
        }
        return null;
    }

    private UserAccount findByEmployeeIntId(int employeeId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_EMPLOYEE_ID)) {

            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByEmployeeId failed for employeeId=" + employeeId, e);
        }
        return null;
    }

   
    // Maps one ResultSet row from v_user_account into a UserAccount object.
    
    private UserAccount mapRow(ResultSet rs) throws SQLException {
        int userAccountId = rs.getInt("user_account_id");
        int employeeId = rs.getInt("employee_id");
        String username = rs.getString("username");
        String passwordHash = rs.getString("password_hash");
        boolean isActive = rs.getBoolean("is_active");
        int roleId = rs.getInt("role_id");   // 0 if NULL (no role assigned)
        Role role = Role.fromId(roleId);

        return new UserAccount(userAccountId, employeeId, username, passwordHash, role, isActive);
    }
}