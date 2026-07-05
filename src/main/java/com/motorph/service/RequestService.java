package com.motorph.service;

import com.motorph.dao.RequestDAO;
import com.motorph.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class RequestService {

    private static final int MAX_SICK_LEAVE = 15;
    private static final int MAX_VACATION_LEAVE = 15;
    private static final int MAX_DEFAULT_LEAVE = 15;

    private static final LocalTime MIN_WORK_TIME = LocalTime.of(9, 0);
    private static final LocalTime MAX_WORK_TIME = LocalTime.of(17, 0);

    private final RequestDAO requestDAO;

    public RequestService(RequestDAO requestDAO) {
        this.requestDAO = requestDAO;
    }

    public void submit(Request request) {
        validate(request);
        requestDAO.save(request);
    }

    public void approve(Request request, int approverId) {
        if (request == null) {
            throw new IllegalArgumentException("Request is required.");
        }

        request.setStatus(RequestStatus.APPROVED);
        request.setApproverId(approverId);
        requestDAO.update(request);
    }

    public void reject(Request request, int approverId) {
        if (request == null) {
            throw new IllegalArgumentException("Request is required.");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setApproverId(approverId);
        requestDAO.update(request);
    }

    public void cancel(Request request) {
        if (request == null) {
            throw new IllegalArgumentException("Request is required.");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending requests can be cancelled. Current status: " + request.getStatus()
            );
        }

        request.setStatus(RequestStatus.CANCELLED);
        requestDAO.update(request);
    }

    public Request findById(int requestId, RequestType type) {
        return requestDAO.findById(requestId, type);
    }

    public List<Request> findByEmployee(int employeeId) {
        return requestDAO.findByEmployeeId(employeeId);
    }

    public List<Request> findByStatus(RequestStatus status) {
        return requestDAO.findByStatus(status);
    }

    public List<Request> findAllPending() {
        return requestDAO.findAllPending();
    }

    public List<Request> findAll() {
        return requestDAO.findAll();
    }

    public List<LeaveRequest> findLeaveByEmployee(int employeeId) {
        return requestDAO.findByEmployeeId(employeeId).stream()
                .filter(r -> r instanceof LeaveRequest)
                .map(r -> (LeaveRequest) r)
                .collect(Collectors.toList());
    }

    public List<OvertimeRequest> findOvertimeByEmployee(int employeeId) {
        return requestDAO.findByEmployeeId(employeeId).stream()
                .filter(r -> r instanceof OvertimeRequest)
                .map(r -> (OvertimeRequest) r)
                .collect(Collectors.toList());
    }

    public List<UndertimeRequest> findUndertimeByEmployee(int employeeId) {
        return requestDAO.findByEmployeeId(employeeId).stream()
                .filter(r -> r instanceof UndertimeRequest)
                .map(r -> (UndertimeRequest) r)
                .collect(Collectors.toList());
    }

    public void delete(int requestId, RequestType type) {
        requestDAO.delete(requestId, type);
    }

    // NEW: persist edits to an existing request. Like approve/reject, this does not
    // re-run submit-time validation (which would reject a request whose date is now
    // in the past), so an existing request can be edited or re-statused.
    public void update(Request request) {
        if (request == null) {
            throw new IllegalArgumentException("Request is required.");
        }
        requestDAO.update(request);
    }

    private void validate(Request request) {
        if (request == null) {
            throw new IllegalArgumentException("Request is required.");
        }

        if (request.getReason() == null || request.getReason().isBlank()) {
            throw new IllegalArgumentException("Reason is required.");
        }

        if (request instanceof LeaveRequest leave) {
            validateLeave(leave);
        } else if (request instanceof OvertimeRequest overtime) {
            validateWorkTime(
                    overtime.getOvertimeDate(),
                    overtime.getOvertimeDate(),
                    overtime.getStartTime(),
                    overtime.getEndTime(),
                    "Overtime"
            );
        } else if (request instanceof UndertimeRequest undertime) {
            validateWorkTime(
                    undertime.getUndertimeDate(),
                    undertime.getUndertimeDate(),
                    undertime.getStartTime(),
                    undertime.getEndTime(),
                    "Undertime"
            );
        } else {
            throw new IllegalArgumentException("Unsupported request type.");
        }
    }

    private void validateLeave(LeaveRequest leave) {
        LocalDate today = LocalDate.now();

        if (leave.getStartDate() == null) {
            throw new IllegalArgumentException("Leave start date is required.");
        }

        if (leave.getEndDate() == null) {
            throw new IllegalArgumentException("Leave end date is required.");
        }

        if (leave.getStartDate().isBefore(today)) {
            throw new IllegalArgumentException("Leave start date cannot be in the past.");
        }

        if (leave.getEndDate().isBefore(today)) {
            throw new IllegalArgumentException("Leave end date cannot be in the past.");
        }

        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new IllegalArgumentException("Leave end date cannot be before the start date.");
        }

        if (leave.getLeaveType() == null) {
            throw new IllegalArgumentException("Leave type is required.");
        }

        long requestedDays = ChronoUnit.DAYS.between(
                leave.getStartDate(),
                leave.getEndDate()
        ) + 1;

        if (requestedDays <= 0) {
            throw new IllegalArgumentException("Leave request must be at least 1 day.");
        }

        String leaveTypeName = leave.getLeaveType().getLeaveTypeName();

        int maxAllowed = getMaxLeaveAllowed(leaveTypeName);
        int usedDays = getUsedLeaveDays(leave.getEmployeeId(), leaveTypeName);
        int remainingDays = maxAllowed - usedDays;

        if (requestedDays > remainingDays) {
            throw new IllegalArgumentException(
                    leaveTypeName + " exceeds available balance. Remaining: "
                            + remainingDays + " day(s)."
            );
        }
    }

    private void validateWorkTime(LocalDate startDate,
                                  LocalDate endDate,
                                  LocalTime startTime,
                                  LocalTime endTime,
                                  String label) {

        LocalDate today = LocalDate.now();

        if (startDate == null) {
            throw new IllegalArgumentException(label + " start date is required.");
        }

        if (endDate == null) {
            throw new IllegalArgumentException(label + " end date is required.");
        }

        if (startDate.isBefore(today)) {
            throw new IllegalArgumentException(label + " start date cannot be in the past.");
        }

        if (endDate.isBefore(today)) {
            throw new IllegalArgumentException(label + " end date cannot be in the past.");
        }

        if (!startDate.equals(endDate)) {
            throw new IllegalArgumentException(
                    "For " + label + ", start date and end date must be the same."
            );
        }

        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException(label + " start and end time are required.");
        }

        if (startTime.isBefore(MIN_WORK_TIME) || startTime.isAfter(MAX_WORK_TIME)
                || endTime.isBefore(MIN_WORK_TIME) || endTime.isAfter(MAX_WORK_TIME)) {
            throw new IllegalArgumentException(label + " time must be between 9 AM and 5 PM.");
        }

        if (startTime.equals(endTime)) {
            throw new IllegalArgumentException(
                    label + " start time and end time cannot be the same."
            );
        }

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException(label + " end time must be after start time.");
        }
    }

    private int getMaxLeaveAllowed(String leaveTypeName) {
        if (leaveTypeName == null) {
            throw new IllegalArgumentException("Leave type is required.");
        }

        if (leaveTypeName.equalsIgnoreCase("Sick Leave")) {
            return MAX_SICK_LEAVE;
        }

        if (leaveTypeName.equalsIgnoreCase("Vacation Leave")) {
            return MAX_VACATION_LEAVE;
        }

        // Other seeded leave types (Emergency, Maternity, Paternity, Solo Parent,
        // Unpaid) don't have a dedicated cap yet, so fall back to the default
        // allowance instead of rejecting the request outright.
        return MAX_DEFAULT_LEAVE;
    }

    private int getUsedLeaveDays(int employeeId, String leaveTypeName) {
        List<Request> requests = requestDAO.findByEmployeeId(employeeId);

        int usedDays = 0;

        for (Request request : requests) {
            if (!(request instanceof LeaveRequest leave)) {
                continue;
            }

            if (leave.getStatus() == RequestStatus.REJECTED
                    || leave.getStatus() == RequestStatus.CANCELLED) {
                continue;
            }

            if (leave.getLeaveType() == null
                    || leave.getLeaveType().getLeaveTypeName() == null) {
                continue;
            }

            if (!leave.getLeaveType().getLeaveTypeName().equalsIgnoreCase(leaveTypeName)) {
                continue;
            }

            if (leave.getStartDate() == null || leave.getEndDate() == null) {
                continue;
            }

            usedDays += (int) ChronoUnit.DAYS.between(
                    leave.getStartDate(),
                    leave.getEndDate()
            ) + 1;
        }

        return usedDays;
    }
}