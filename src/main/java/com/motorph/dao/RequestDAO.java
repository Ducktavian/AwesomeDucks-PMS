package com.motorph.dao;

import com.motorph.model.Request;
import com.motorph.model.RequestStatus;

import java.util.List;

/**
 *
 * @author Ducktavian
 */
public interface RequestDAO {
    Request findById(int requestId, com.motorph.model.RequestType type);
    List<Request> findByEmployeeId(int employeeId);
    List<Request> findByStatus(RequestStatus status);
    List<Request> findAll();
    void save(Request request);
    void update(Request request);
    void delete(int requestId, com.motorph.model.RequestType type);
}