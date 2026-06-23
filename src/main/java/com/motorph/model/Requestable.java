package com.motorph.model;

import java.time.LocalDateTime;

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
