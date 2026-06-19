package com.motorph.model;

import java.time.LocalDateTime;

/**
 *
 * @author Ducktavian
 */
public interface Requestable {
    int getRequestId();
    RequestType getRequestType();
    int getEmployeeId();
    LocalDateTime getDateFiled();
    RequestStatus getStatus();
    String getReason();
    Integer getApproverId();
    
    void setStatus(RequestStatus status);
    void setApproverId(Integer approverId);
}
