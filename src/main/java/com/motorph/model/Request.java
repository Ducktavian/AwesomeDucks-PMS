package com.motorph.model;

import java.time.LocalDate;

/**
 *
 * @author Ducktavian
 */
public class Request implements Requestable {
    protected String requestId;
    protected String employeeId;
    protected RequestStatus status;
    protected String approverId;
    protected String reason;
    protected LocalDate dateFiled;
    protected RequestType requestType;
    
    public Request(String requestId, String employeeId, RequestStatus status, String approverId, String reason, LocalDate dateFiled, RequestType requestType) {
        this.requestId = requestId;
        this.employeeId = employeeId;
        this.status = status;
        this.approverId = approverId;
        this.reason = reason;
        this.dateFiled = dateFiled;
        this.requestType = requestType;
    }
    
    //Overide

    @Override
    public String getRequestId() {
        return requestId;
    }

    @Override
    public RequestType getRequestType() {
        return requestType;
    }

    @Override
    public String getEmployeeId() {
        return employeeId;
    }

    @Override
    public LocalDate getDateFiled() {
        return dateFiled;
    }

    @Override
    public RequestStatus getStatus() {
        return status;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public String getApprovedBy() {
        return approverId;
    }

    @Override
    public void approve(String approverId) {
       this.status = RequestStatus.APPROVED;
    }

    @Override
    public void deny(String approverId) {
        this.status = RequestStatus.DENIED;
    }
    
    
    // HMMM??
    
    public void setRequestStatus(RequestStatus status) {
        this.status = status;
    }
    
    public void setApprovedBy(String approverId) {
        this.approverId = approverId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    
    
}
