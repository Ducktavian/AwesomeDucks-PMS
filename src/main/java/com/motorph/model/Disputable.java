package com.motorph.model;

import java.time.LocalDate;

public interface Disputable {
    String getDisputeId();
    String getEmployeeId();
    LocalDate getDateFiled();
    DisputeStatus getStatus();
    String getReason();
    Integer getReviewedById();
    DisputeType getDisputeType();
}
