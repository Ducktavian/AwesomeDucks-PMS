package com.motorph.model;

import java.time.LocalDate;

/**
 *
 * @author Ducktavian
 */
public interface Disputable {
    String getDisputeId();
    String getEmployeeId();
    LocalDate getDateFiled();
    DisputeStatus getStatus();
    String getReason();
    String getReviewedById();
    DisputeType getDisputeType();
}
