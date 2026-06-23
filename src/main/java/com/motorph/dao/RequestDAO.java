package com.motorph.dao;

import com.motorph.model.Request;
import com.motorph.model.RequestStatus;
import com.motorph.model.RequestType;

import java.util.List;

public interface RequestDAO {
    Request findById(int requestId, RequestType type);
    List<Request> findByEmployeeId(int employeeId);
    List<Request> findByStatus(RequestStatus status);
    List<Request> findAll();
    void save(Request request);
    void update(Request request);
    void delete(int requestId, RequestType type);

    // Uses v_pending_requests - returns all pending leave and work-time requests combined.
    List<Request> findAllPending();
}
