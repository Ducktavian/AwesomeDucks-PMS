package com.motorph.service;

import com.motorph.dao.RequestDAO;
import com.motorph.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


public class RequestService {

    private final RequestDAO requestDAO;

    public RequestService(RequestDAO requestDAO) {
        this.requestDAO = requestDAO;
    }

    // -----------------------------------------------------------------------
    // Submit
    // -----------------------------------------------------------------------

   
  
    // Works for LeaveRequest, OvertimeRequest, and UndertimeRequest.
    // @throws IllegalArgumentException if the request fails validation
    public void submit(Request request) {
        validate(request);
        requestDAO.save(request);
    }


    // Approve / Reject
    public void approve(Request request, int approverId) {
        request.setStatus(RequestStatus.APPROVED);
        request.setApproverId(approverId);
        requestDAO.update(request);
    }


    // Reject a request. Sets status to REJECTED and records the approver.
    public void reject(Request request, int approverId) {
        request.setStatus(RequestStatus.REJECTED);
        request.setApproverId(approverId);
        requestDAO.update(request);
    }

 
     //Cancel a request. Only the employee who filed it should be able to do this - enforce that access check at the controller/UI layer.
    public void cancel(Request request) {
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending requests can be cancelled. Current status: " + request.getStatus());
        }
        request.setStatus(RequestStatus.CANCELLED);
        requestDAO.update(request);
    }

    
    // Retrieval
    public Request findById(int requestId, RequestType type) {
        return requestDAO.findById(requestId, type);
    }

    /** All requests (all types, all statuses) for one employee. */
    public List<Request> findByEmployee(int employeeId) {
        return requestDAO.findByEmployeeId(employeeId);
    }

    /** All requests across all employees with a specific status. */
    public List<Request> findByStatus(RequestStatus status) {
        return requestDAO.findByStatus(status);
    }

    /** All pending requests — convenience method for approval dashboards. */
    public List<Request> findAllPending() {
        return requestDAO.findByStatus(RequestStatus.PENDING);
    }

    /** All requests of any type or status. */
    public List<Request> findAll() {
        return requestDAO.findAll();
    }


    // Typed retrieval — when callers need a specific subtype list
    /** All leave requests for one employee. */
    public List<LeaveRequest> findLeaveByEmployee(int employeeId) {
        return requestDAO.findByEmployeeId(employeeId).stream()
                .filter(r -> r instanceof LeaveRequest)
                .map(r -> (LeaveRequest) r)
                .collect(Collectors.toList());
    }

    /** All overtime requests for one employee. */
    public List<OvertimeRequest> findOvertimeByEmployee(int employeeId) {
        return requestDAO.findByEmployeeId(employeeId).stream()
                .filter(r -> r instanceof OvertimeRequest)
                .map(r -> (OvertimeRequest) r)
                .collect(Collectors.toList());
    }

    /** All undertime requests for one employee. */
    public List<UndertimeRequest> findUndertimeByEmployee(int employeeId) {
        return requestDAO.findByEmployeeId(employeeId).stream()
                .filter(r -> r instanceof UndertimeRequest)
                .map(r -> (UndertimeRequest) r)
                .collect(Collectors.toList());
    }


    // Delete
    public void delete(int requestId, RequestType type) {
        requestDAO.delete(requestId, type);
    }


    // Validation
    private void validate(Request request) {
        if (request instanceof LeaveRequest leave) {
            validateLeave(leave);
        } else if (request instanceof OvertimeRequest overtime) {
            validateWorkTime(overtime.getOvertimeDate(), overtime.getStartTime(), overtime.getEndTime(), "Overtime");
        } else if (request instanceof UndertimeRequest undertime) {
            validateWorkTime(undertime.getUndertimeDate(), undertime.getStartTime(), undertime.getEndTime(), "Undertime");
        }
    }

    private void validateLeave(LeaveRequest leave) {
        LocalDate today = LocalDate.now();

        if (leave.getStartDate().isBefore(today)) {
            throw new IllegalArgumentException("Leave start date cannot be in the past.");
        }
        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new IllegalArgumentException("Leave end date cannot be before the start date.");
        }
        if (leave.getLeaveType() == null) {
            throw new IllegalArgumentException("Leave type is required.");
        }
    }

    private void validateWorkTime(LocalDate requestDate,
                                  java.time.LocalTime startTime,
                                  java.time.LocalTime endTime,
                                  String label) {
        if (requestDate == null) {
            throw new IllegalArgumentException(label + " date is required.");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException(label + " start and end time are required.");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException(
                    label + " end time must be after start time.");
        }
    }
}