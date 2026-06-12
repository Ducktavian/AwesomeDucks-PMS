package com.motorph.model;

import java.time.LocalDate;

/**
 *
 * @author Ducktavian
 */
public interface Requestable {
    String getRequestId();
    RequestType getRequestType();
    String getEmployeeId();
    LocalDate getDateFiled();
    RequestStatus getStatus();
    String getReason();
    String getApprovedBy();
    void approve(String approverId);
    void deny(String approverId);
}
