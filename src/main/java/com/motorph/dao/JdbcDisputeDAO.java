package com.motorph.dao;

import com.motorph.config.DatabaseConnection;
import com.motorph.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcDisputeDAO implements DisputeDAO {

    // Reads join employee names via the view.
    private static final String BASE_SELECT =
            "SELECT dispute_id, employee_id, employee_name, dispute_type, reason, " +
            "       dispute_status, reviewed_by_id, date_filed, date_reviewed, " +
            "       category, target_field, payslip_number " +
            "FROM v_dispute ";

    private static final String SELECT_ALL = BASE_SELECT + "ORDER BY date_filed DESC";
    private static final String SELECT_BY_ID = BASE_SELECT + "WHERE dispute_id = ?";
    private static final String SELECT_BY_EMPLOYEE = BASE_SELECT + "WHERE employee_id = ? ORDER BY date_filed DESC";
    private static final String SELECT_BY_STATUS = BASE_SELECT + "WHERE dispute_status = ? ORDER BY date_filed DESC";

    private static final String INSERT =
            "INSERT INTO dispute (employee_id, dispute_type, reason, dispute_status, " +
            "    reviewed_by_id, date_filed, date_reviewed, category, target_field, payslip_number) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE dispute SET dispute_status = ?, reviewed_by_id = ?, date_reviewed = ? " +
            "WHERE dispute_id = ?";

    private static final String DELETE = "DELETE FROM dispute WHERE dispute_id = ?";

    // -----------------------------------------------------------------------

    @Override
    public Dispute findById(String disputeId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, Integer.parseInt(disputeId));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById failed for disputeId=" + disputeId, e);
        }
        return null;
    }

    @Override
    public List<Dispute> findByEmployeeId(String employeeId) {
        List<Dispute> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_EMPLOYEE)) {
            ps.setInt(1, Integer.parseInt(employeeId));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByEmployeeId failed for id=" + employeeId, e);
        }
        return list;
    }

    @Override
    public List<Dispute> findByStatus(DisputeStatus status) {
        List<Dispute> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_STATUS)) {
            ps.setString(1, status.getDbValue());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByStatus failed for status=" + status, e);
        }
        return list;
    }

    @Override
    public List<Dispute> findAll() {
        List<Dispute> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("findAll failed", e);
        }
        return list;
    }

    @Override
    public void save(Dispute dispute) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt   (1, Integer.parseInt(dispute.getEmployeeId()));
            ps.setString(2, dispute.getDisputeType().name());
            ps.setString(3, dispute.getReason());
            ps.setString(4, dispute.getStatus().getDbValue());

            Integer reviewedBy = dispute.getReviewedById();
            if (reviewedBy != null) ps.setInt(5, reviewedBy);
            else                    ps.setNull(5, Types.INTEGER);

            ps.setDate(6, dispute.getDateFiled() != null
                    ? Date.valueOf(dispute.getDateFiled()) : Date.valueOf(LocalDate.now()));

            if (dispute.getDateReviewed() != null) {
                ps.setDate(7, Date.valueOf(dispute.getDateReviewed()));
            } else {
                ps.setNull(7, Types.DATE);
            }

            if (dispute instanceof InformationDispute info) {
                ps.setString(8, info.getCategory());
                ps.setString(9, info.getTargetField());
                ps.setNull  (10, Types.VARCHAR);
            } else if (dispute instanceof PayrollDispute pd) {
                ps.setNull  (8, Types.VARCHAR);
                ps.setNull  (9, Types.VARCHAR);
                ps.setString(10, pd.getPayslipNumber());
            } else {
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.VARCHAR);
                ps.setNull(10, Types.VARCHAR);
            }

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) dispute.setDisputeId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("save(Dispute) failed", e);
        }
    }

    /** Updates only the review-lifecycle fields: status, reviewer, date reviewed. */
    @Override
    public void update(Dispute dispute) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, dispute.getStatus().getDbValue());

            Integer reviewedBy = dispute.getReviewedById();
            if (reviewedBy != null) ps.setInt(2, reviewedBy);
            else                    ps.setNull(2, Types.INTEGER);

            ps.setDate(3, dispute.getDateReviewed() != null
                    ? Date.valueOf(dispute.getDateReviewed()) : null);

            ps.setInt(4, dispute.getDisputeIntId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("update(Dispute) failed for id=" + dispute.getDisputeIntId(), e);
        }
    }

    @Override
    public void delete(String disputeId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, Integer.parseInt(disputeId));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("delete failed for disputeId=" + disputeId, e);
        }
    }

    @Override
    public void save(Object entity) {
        if (entity instanceof Dispute) save((Dispute) entity);
        else throw new IllegalArgumentException("Expected Dispute, got: " + entity.getClass().getName());
    }

    @Override
    public void update(Object entity) {
        if (entity instanceof Dispute) update((Dispute) entity);
        else throw new IllegalArgumentException("Expected Dispute, got: " + entity.getClass().getName());
    }

    // -----------------------------------------------------------------------

    private Dispute mapRow(ResultSet rs) throws SQLException {
        int disputeId = rs.getInt("dispute_id");
        String employeeId = String.valueOf(rs.getInt("employee_id"));
        String type = rs.getString("dispute_type");
        String reason = rs.getString("reason");
        DisputeStatus status = DisputeStatus.fromDbValue(rs.getString("dispute_status"));

        int     reviewedByRaw = rs.getInt("reviewed_by_id");
        Integer reviewedById = rs.wasNull() ? null : reviewedByRaw;

        Date filedDate = rs.getDate("date_filed");
        Date reviewedDate = rs.getDate("date_reviewed");
        LocalDate dateFiled = filedDate != null ? filedDate.toLocalDate() : LocalDate.now();
        LocalDate dateReviewed = reviewedDate != null ? reviewedDate.toLocalDate() : null;

        if ("INFORMATION_DISPUTE".equals(type)) {
            String category = rs.getString("category");
            String targetField = rs.getString("target_field");
            return new InformationDispute(
                    disputeId, employeeId, reason, status, reviewedById,
                    dateFiled, dateReviewed, category, targetField);
        } else {
            String payslipNumber = rs.getString("payslip_number");
            return new PayrollDispute(
                    disputeId, employeeId, reason, status, reviewedById,
                    dateFiled, dateReviewed, payslipNumber);
        }
    }
}
