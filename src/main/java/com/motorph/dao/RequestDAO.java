package com.motorph.dao;

import com.motorph.model.Request;
import com.motorph.model.RequestStatus;
import java.util.List;

/**
 *
 * @author Ducktavian
 */
public interface RequestDAO extends BaseDAO {
    Request findById(String requestId);
    List<Request> findByEmployeeId(String employeeId);
    List<Request> findByStatus(RequestStatus status);
    List<Request> findAll();
    void save(Request request);
    void update(Request request);
    void delete(String requestId);
}
