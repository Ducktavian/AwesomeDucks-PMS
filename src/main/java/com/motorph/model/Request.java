package com.motorph.model;

import java.time.LocalDateTime;

public abstract class Request implements Requestable {

    protected int requestId;
    protected int employeeId;
    protected RequestStatus status;
    protected Integer approverId;
    protected String reason;
    protected LocalDateTime dateFiled;
    protected RequestType requestType;

    protected Request(int requestId, int employeeId, RequestStatus status,
                      Integer approverId, String reason,
                      LocalDateTime dateFiled, RequestType requestType) {
        this.requestId = requestId;
        this.employeeId = employeeId;
        this.status = status;
        this.approverId = approverId;
        this.reason = reason;
        this.dateFiled = dateFiled;
        this.requestType = requestType;
    }

    @Override public int getRequestId() { return requestId; }
    @Override public int getEmployeeId() { return employeeId; }
    @Override public RequestStatus getStatus() { return status; }
    @Override public Integer getApproverId() { return approverId; }
    @Override public String getReason() { return reason; }
    @Override public LocalDateTime getDateFiled() { return dateFiled; }
    @Override public RequestType getRequestType() { return requestType; }

    @Override public void setStatus(RequestStatus status) { this.status = status; }
    @Override public void setApproverId(Integer approverId) { this.approverId = approverId; }
}
