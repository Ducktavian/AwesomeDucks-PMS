package com.motorph.dao;

import com.motorph.model.Dispute;
import com.motorph.model.DisputeStatus;
import java.util.List;

/**
 *
 * @author Ducktavian
 */
public interface DisputeDAO extends BaseDAO {
    Dispute findById(String disputeId);
    List<Dispute> findByEmployeeId(String employeeId);
    List<Dispute> findByStatus(DisputeStatus status);
    List<Dispute> findAll();
    void save(Dispute dispute);
    void update(Dispute dispute);
    void delete(String disputeId);
}
