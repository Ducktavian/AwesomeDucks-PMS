package com.motorph.dao;

import com.motorph.config.DatabaseConnection;
import com.motorph.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class JdbcRequestDAO implements RequestDAO {


    // SQL - leave_request (reads via v_leave_requests)
    private static final String SELECT_LEAVE_VIEW = """
            SELECT leave_request_id, employee_id, request_status_type,
                   approved_by, description, created_at,
                   start_date, end_date, leave_type_id, leave_type_name
            FROM v_leave_requests
            """;

    private static final String SQL_FIND_LEAVE_BY_ID =
            SELECT_LEAVE_VIEW + " WHERE leave_request_id = ?";

    private static final String SQL_FIND_LEAVE_BY_EMPLOYEE =
            SELECT_LEAVE_VIEW + " WHERE employee_id = ? ORDER BY created_at DESC";

    private static final String SQL_FIND_LEAVE_BY_STATUS =
            SELECT_LEAVE_VIEW + " WHERE request_status_type = ? ORDER BY created_at DESC";

    private static final String SQL_FIND_ALL_LEAVE =
            SELECT_LEAVE_VIEW + " ORDER BY created_at DESC";

    private static final String SQL_INSERT_LEAVE = """
            INSERT INTO leave_request
              (employee_id, leave_type_id, start_date, end_date,
               description, request_status_id, approved_by)
            VALUES (?, ?,
                    ?, ?, ?,
                    (SELECT request_status_id FROM request_status WHERE request_status_type = ?),
                    ?)
            """;

    private static final String SQL_UPDATE_LEAVE = """
            UPDATE leave_request
            SET request_status_id = (SELECT request_status_id
                                     FROM request_status
                                     WHERE request_status_type = ?),
                approved_by  = ?,
                approved_at  = CASE WHEN ? IN ('Approved','Rejected') THEN NOW() ELSE NULL END
            WHERE leave_request_id = ?
            """;

    private static final String SQL_DELETE_LEAVE =
            "DELETE FROM leave_request WHERE leave_request_id = ?";


    // SQL — work_time_request (reads via v_work_time_requests)
    private static final String SELECT_WTR_VIEW = """
            SELECT work_time_request_id, employee_id, request_status_type,
                   approved_by, reason, created_at,
                   request_type, request_date, start_time, end_time
            FROM v_work_time_requests
            """;

    private static final String SQL_FIND_WTR_BY_ID =
            SELECT_WTR_VIEW + " WHERE work_time_request_id = ?";

    private static final String SQL_FIND_WTR_BY_EMPLOYEE =
            SELECT_WTR_VIEW + " WHERE employee_id = ? ORDER BY created_at DESC";

    private static final String SQL_FIND_WTR_BY_STATUS =
            SELECT_WTR_VIEW + " WHERE request_status_type = ? ORDER BY created_at DESC";

    private static final String SQL_FIND_ALL_WTR =
            SELECT_WTR_VIEW + " ORDER BY created_at DESC";

    private static final String SQL_INSERT_WTR = """
            INSERT INTO work_time_request
              (employee_id, request_type, request_date,
               start_time, end_time, reason, request_status_id, approved_by)
            VALUES (?, ?, ?, ?, ?, ?,
                    (SELECT request_status_id FROM request_status WHERE request_status_type = ?),
                    ?)
            """;

    private static final String SQL_UPDATE_WTR = """
            UPDATE work_time_request
            SET request_status_id = (SELECT request_status_id
                                     FROM request_status
                                     WHERE request_status_type = ?),
                approved_by  = ?,
                approved_at  = CASE WHEN ? IN ('Approved','Rejected') THEN NOW() ELSE NULL END
            WHERE work_time_request_id = ?
            """;

    private static final String SQL_DELETE_WTR =
            "DELETE FROM work_time_request WHERE work_time_request_id = ?";


    // Public API
    @Override
    public Request findById(int requestId, RequestType type) {
        if (type == RequestType.LEAVE) {
            return findLeaveById(requestId);
        } else {
            return findWorkTimeById(requestId);
        }
    }

    @Override
    public List<Request> findByEmployeeId(int employeeId) {
        List<Request> results = new ArrayList<>();
        results.addAll(findLeaveByEmployee(employeeId));
        results.addAll(findWorkTimeByEmployee(employeeId));
        results.sort((a, b) -> b.getDateFiled().compareTo(a.getDateFiled()));
        return results;
    }

    @Override
    public List<Request> findByStatus(RequestStatus status) {
        List<Request> results = new ArrayList<>();
        results.addAll(findLeaveByStatus(status));
        results.addAll(findWorkTimeByStatus(status));
        results.sort((a, b) -> b.getDateFiled().compareTo(a.getDateFiled()));
        return results;
    }

    @Override
    public List<Request> findAll() {
        List<Request> results = new ArrayList<>();
        results.addAll(findAllLeave());
        results.addAll(findAllWorkTime());
        results.sort((a, b) -> b.getDateFiled().compareTo(a.getDateFiled()));
        return results;
    }

    @Override
    public void save(Request request) {
        if (request instanceof LeaveRequest leave) {
            saveLeave(leave);
        } else if (request instanceof OvertimeRequest overtime) {
            saveWorkTime(overtime.getEmployeeId(), RequestType.OVERTIME,
                    overtime.getOvertimeDate(), overtime.getStartTime(), overtime.getEndTime(),
                    overtime.getReason(), overtime.getStatus(), overtime.getApproverId());
        } else if (request instanceof UndertimeRequest undertime) {
            saveWorkTime(undertime.getEmployeeId(), RequestType.UNDERTIME,
                    undertime.getUndertimeDate(), undertime.getStartTime(), undertime.getEndTime(),
                    undertime.getReason(), undertime.getStatus(), undertime.getApproverId());
        } else {
            throw new IllegalArgumentException("Unknown Request subtype: " + request.getClass());
        }
    }

    @Override
    public void update(Request request) {
        if (request instanceof LeaveRequest) {
            updateLeave(request);
        } else {
            updateWorkTime(request);
        }
    }

    @Override
    public void delete(int requestId, RequestType type) {
        String sql = (type == RequestType.LEAVE) ? SQL_DELETE_LEAVE : SQL_DELETE_WTR;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete request id=" + requestId, e);
        }
    }


    // Leave helpers
    private LeaveRequest findLeaveById(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_LEAVE_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapLeave(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("findLeaveById failed for id=" + id, e);
        }
        return null;
    }

    private List<Request> findLeaveByEmployee(int employeeId) {
        return queryLeave(SQL_FIND_LEAVE_BY_EMPLOYEE, ps -> ps.setInt(1, employeeId));
    }

    private List<Request> findLeaveByStatus(RequestStatus status) {
        return queryLeave(SQL_FIND_LEAVE_BY_STATUS, ps -> ps.setString(1, status.toDbValue()));
    }

    private List<Request> findAllLeave() {
        return queryLeave(SQL_FIND_ALL_LEAVE, ps -> {});
    }

    private List<Request> queryLeave(String sql, StatementPreparer preparer) {
        List<Request> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            preparer.prepare(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapLeave(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Leave query failed: " + sql, e);
        }
        return list;
    }

    private LeaveRequest mapLeave(ResultSet rs) throws SQLException {
        Integer approverId = rs.getObject("approved_by") != null
                ? rs.getInt("approved_by") : null;

        Timestamp createdAt = rs.getTimestamp("created_at");
        LocalDateTime dateFiled = createdAt != null ? createdAt.toLocalDateTime() : null;

        LeaveType leaveType = new LeaveType(
                rs.getInt("leave_type_id"),
                rs.getString("leave_type_name")
        );

        return new LeaveRequest(
                rs.getInt("leave_request_id"),
                rs.getInt("employee_id"),
                RequestStatus.fromDbValue(rs.getString("request_status_type")),
                approverId,
                rs.getString("description"),   // leave_request.description → reason
                dateFiled,
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                leaveType
        );
    }

    private void saveLeave(LeaveRequest leave) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT_LEAVE,
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, leave.getEmployeeId());
            ps.setInt(2, leave.getLeaveType().getLeaveTypeId());
            ps.setDate(3, Date.valueOf(leave.getStartDate()));
            ps.setDate(4, Date.valueOf(leave.getEndDate()));
            ps.setString(5, leave.getReason());
            ps.setString(6, leave.getStatus().toDbValue());
            if (leave.getApproverId() != null) {
                ps.setInt(7, leave.getApproverId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("saveLeave failed", e);
        }
    }

    private void updateLeave(Request request) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_LEAVE)) {
            String statusValue = request.getStatus().toDbValue();
            ps.setString(1, statusValue);
            if (request.getApproverId() != null) {
                ps.setInt(2, request.getApproverId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, statusValue);   // used in CASE for approved_at
            ps.setInt(4, request.getRequestId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("updateLeave failed for id=" + request.getRequestId(), e);
        }
    }


    // WorkTime (overtime / undertime) helpers
    private Request findWorkTimeById(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_WTR_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapWorkTime(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("findWorkTimeById failed for id=" + id, e);
        }
        return null;
    }

    private List<Request> findWorkTimeByEmployee(int employeeId) {
        return queryWorkTime(SQL_FIND_WTR_BY_EMPLOYEE, ps -> ps.setInt(1, employeeId));
    }

    private List<Request> findWorkTimeByStatus(RequestStatus status) {
        return queryWorkTime(SQL_FIND_WTR_BY_STATUS, ps -> ps.setString(1, status.toDbValue()));
    }

    private List<Request> findAllWorkTime() {
        return queryWorkTime(SQL_FIND_ALL_WTR, ps -> {});
    }

    private List<Request> queryWorkTime(String sql, StatementPreparer preparer) {
        List<Request> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            preparer.prepare(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapWorkTime(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("WorkTime query failed: " + sql, e);
        }
        return list;
    }

    private Request mapWorkTime(ResultSet rs) throws SQLException {
        int requestId = rs.getInt("work_time_request_id");
        int employeeId = rs.getInt("employee_id");
        RequestStatus status = RequestStatus.fromDbValue(rs.getString("request_status_type"));
        Integer approverId = rs.getObject("approved_by") != null ? rs.getInt("approved_by") : null;
        String  reason = rs.getString("reason");

        Timestamp createdAt  = rs.getTimestamp("created_at");
        LocalDateTime dateFiled = createdAt != null ? createdAt.toLocalDateTime() : null;

        RequestType type = RequestType.fromDbValue(rs.getString("request_type"));
        LocalDate reqDate = rs.getDate("request_date").toLocalDate();
        LocalTime start = rs.getTime("start_time").toLocalTime();
        LocalTime end = rs.getTime("end_time").toLocalTime();

        if (type == RequestType.OVERTIME) {
            return new OvertimeRequest(requestId, employeeId, status, approverId,
                    reason, dateFiled, reqDate, start, end);
        } else {
            return new UndertimeRequest(requestId, employeeId, status, approverId,
                    reason, dateFiled, reqDate, start, end);
        }
    }

    private void saveWorkTime(int employeeId, RequestType type,
                              LocalDate date, LocalTime start, LocalTime end,
                              String reason, RequestStatus status, Integer approverId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT_WTR,
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, employeeId);
            ps.setString(2, type.toDbValue());
            ps.setDate(3, Date.valueOf(date));
            ps.setTime(4, Time.valueOf(start));
            ps.setTime(5, Time.valueOf(end));
            ps.setString(6, reason);
            ps.setString(7, status.toDbValue());
            if (approverId != null) {
                ps.setInt(8, approverId);
            } else {
                ps.setNull(8, Types.INTEGER);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("saveWorkTime failed", e);
        }
    }

    private void updateWorkTime(Request request) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_WTR)) {
            String statusValue = request.getStatus().toDbValue();
            ps.setString(1, statusValue);
            if (request.getApproverId() != null) {
                ps.setInt(2, request.getApproverId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, statusValue);
            ps.setInt(4, request.getRequestId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("updateWorkTime failed for id=" + request.getRequestId(), e);
        }
    }

    // Uses v_pending_requests — UNION of all pending leave + work-time in one query.
    @Override
    public List<Request> findAllPending() {
        String sql = """
                SELECT request_category, request_id, employee_id,
                       request_type, request_date, start_time, end_time, reason
                FROM v_pending_requests
                ORDER BY request_date ASC
                """;
        List<Request> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String category = rs.getString("request_category");
                int id = rs.getInt("request_id");
                if ("leave".equals(category)) {
                    // Fetch the full leave record from v_leave_requests for the complete model.
                    LeaveRequest full = findLeaveById(id);
                    if (full != null) results.add(full);
                } else {
                    Request full = findWorkTimeById(id);
                    if (full != null) results.add(full);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("findAllPending failed", e);
        }
        return results;
    }

    // Utility
    @FunctionalInterface
    private interface StatementPreparer {
        void prepare(PreparedStatement ps) throws SQLException;
    }
}