package com.motorph.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Base class for all employee requests (leave, overtime, undertime).
 *
 * DB alignment notes:
 *  - requestId / employeeId / approverId are int to match INT PKs in DB.
 *  - dateFiled maps to created_at (neither leave_request nor work_time_request
 *    has a dedicated date_filed column).
 *  - reason maps to leave_request.description / work_time_request.reason.
 */
public abstract class Request implements Requestable {

    protected int requestId;
    protected int employeeId;
    protected RequestStatus status;
    protected Integer approverId;   // nullable — not yet approved
    protected String reason;
    protected LocalDateTime dateFiled; // maps to created_at
    protected RequestType requestType;

    protected Request(int requestId,
                      int employeeId,
                      RequestStatus status,
                      Integer approverId,
                      String reason,
                      LocalDateTime dateFiled,
                      RequestType requestType) {
        this.requestId   = requestId;
        this.employeeId  = employeeId;
        this.status      = status;
        this.approverId  = approverId;
        this.reason      = reason;
        this.dateFiled   = dateFiled;
        this.requestType = requestType;
    }
    
    @Override
    public int getRequestId()           { return requestId; }
    @Override
    public int getEmployeeId()          { return employeeId; }
    @Override
    public RequestStatus getStatus()    { return status; }
    @Override
    public Integer getApproverId()      { return approverId; }
    @Override
    public String getReason()           { return reason; }
    @Override
    public LocalDateTime getDateFiled() { return dateFiled; }
    @Override
    public RequestType getRequestType() { return requestType; }
    @Override
    public void setStatus(RequestStatus status)     { this.status = status; }
    @Override
    public void setApproverId(Integer approverId)   { this.approverId = approverId; }
}