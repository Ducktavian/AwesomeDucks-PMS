package com.motorph.dao;

import com.motorph.config.DatabaseConnection;
import com.motorph.model.Attendance;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcAttendanceDAO implements AttendanceDAO {

    // Joins employee so the model can carry first/last name without a second query.
    private static final String BASE_SELECT =
            "SELECT a.attendance_id, a.employee_id, e.last_name, e.first_name, " +
            "       a.attendance_date, a.time_in, a.time_out, a.attendance_status_id " +
            "FROM attendance a " +
            "JOIN employee e ON a.employee_id = e.employee_id ";

    private static final String SELECT_BY_ID =
            BASE_SELECT + "WHERE a.attendance_id = ?";

    private static final String SELECT_BY_EMPLOYEE_ID =
            BASE_SELECT + "WHERE a.employee_id = ? ORDER BY a.attendance_date ASC";

    private static final String SELECT_ALL =
            BASE_SELECT + "ORDER BY a.employee_id, a.attendance_date ASC";

    // Open session = timed in today but time_out is still NULL
    private static final String SELECT_OPEN_SESSION =
            BASE_SELECT + "WHERE a.employee_id = ? AND a.attendance_date = CURDATE() AND a.time_out IS NULL";

    private static final String INSERT =
            "INSERT INTO attendance (employee_id, attendance_date, time_in, time_out, attendance_status_id) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE attendance SET attendance_date = ?, time_in = ?, time_out = ?, attendance_status_id = ? " +
            "WHERE attendance_id = ?";

    private static final String DELETE =
            "DELETE FROM attendance WHERE attendance_id = ?";

    // -----------------------------------------------------------------------

    @Override
    public Attendance findById(String attendanceId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, Integer.parseInt(attendanceId));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById failed for id=" + attendanceId, e);
        }
        return null;
    }

    @Override
    public List<Attendance> findByEmployeeId(String employeeId) {
        List<Attendance> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_EMPLOYEE_ID)) {
            ps.setInt(1, Integer.parseInt(employeeId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByEmployeeId failed for employeeId=" + employeeId, e);
        }
        return list;
    }

    @Override
    public List<Attendance> findAll() {
        List<Attendance> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("findAll failed", e);
        }
        return list;
    }

    /**
     * Returns today's open attendance record for the employee (time_out IS NULL),
     * or null if the employee has not timed in today.
     */
    @Override
    public Attendance findOpenSession(String employeeId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_OPEN_SESSION)) {
            ps.setInt(1, Integer.parseInt(employeeId));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("findOpenSession failed for employeeId=" + employeeId, e);
        }
        return null;
    }

    /**
     * Inserts a new time-in record. The DB UNIQUE KEY on (employee_id, attendance_date)
     * will reject a duplicate - the service layer checks first to give a friendlier error.
     */
    @Override
    public void save(Attendance attendance) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, Integer.parseInt(attendance.getEmployeeId()));
            ps.setDate(2, Date.valueOf(attendance.getDate()));
            ps.setTime(3, Time.valueOf(attendance.getLogIn()));
            if (attendance.getLogOut() != null) {
                ps.setTime(4, Time.valueOf(attendance.getLogOut()));
            } else {
                ps.setNull(4, Types.TIME);
            }
            if (attendance.getAttendanceStatusId() != null) {
                ps.setInt(5, attendance.getAttendanceStatusId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("save(Attendance) failed for employeeId=" + attendance.getEmployeeId(), e);
        }
    }

    /**
     * Updates time_out and attendance_status_id for an existing record.
     * Used by time-out flow.
     */
    @Override
    public void update(Attendance attendance) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setDate(1, Date.valueOf(attendance.getDate()));
            ps.setTime(2, Time.valueOf(attendance.getLogIn()));
            if (attendance.getLogOut() != null) {
                ps.setTime(3, Time.valueOf(attendance.getLogOut()));
            } else {
                ps.setNull(3, Types.TIME);
            }
            if (attendance.getAttendanceStatusId() != null) {
                ps.setInt(4, attendance.getAttendanceStatusId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setInt(5, attendance.getAttendanceId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("update(Attendance) failed for id=" + attendance.getAttendanceId(), e);
        }
    }

    @Override
    public void delete(String attendanceId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, Integer.parseInt(attendanceId));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("delete failed for id=" + attendanceId, e);
        }
    }

    // Uses v_attendance_summary — aggregate total hours for one employee in a given month.
    @Override
    public double computeMonthlyHours(String employeeId, int year, int month) {
        String sql = "SELECT total_hours_worked FROM v_attendance_summary " +
                     "WHERE employee_id = ? AND work_year = ? AND work_month = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(employeeId));
            ps.setInt(2, year);
            ps.setInt(3, month);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("total_hours_worked") : 0.0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("computeMonthlyHours failed for employeeId=" + employeeId, e);
        }
    }

    // BaseDAO generic overloads
    @Override
    public void save(Object entity) {
        if (entity instanceof Attendance) save((Attendance) entity);
        else throw new IllegalArgumentException("Expected Attendance, got: " + entity.getClass().getName());
    }

    @Override
    public void update(Object entity) {
        if (entity instanceof Attendance) update((Attendance) entity);
        else throw new IllegalArgumentException("Expected Attendance, got: " + entity.getClass().getName());
    }

    // -----------------------------------------------------------------------

    private Attendance mapRow(ResultSet rs) throws SQLException {
        int attendanceId = rs.getInt("attendance_id");
        String employeeId = String.valueOf(rs.getInt("employee_id"));
        String lastName = rs.getString("last_name");
        String firstName = rs.getString("first_name");
        LocalDate date  = rs.getDate("attendance_date").toLocalDate();
        java.sql.Time timeInSql = rs.getTime("time_in");
        java.sql.Time timeOutSql = rs.getTime("time_out");
        int statusId = rs.getInt("attendance_status_id");
        Integer attendanceStatusId = rs.wasNull() ? null : statusId;

        return new Attendance(
                attendanceId,
                employeeId,
                lastName,
                firstName,
                date,
                timeInSql  != null ? timeInSql.toLocalTime()  : null,
                timeOutSql != null ? timeOutSql.toLocalTime() : null,
                attendanceStatusId
        );
    }
}
