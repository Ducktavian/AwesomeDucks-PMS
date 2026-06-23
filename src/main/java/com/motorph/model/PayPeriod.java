package com.motorph.model;

import java.time.LocalDate;

public class PayPeriod {

    private int payPeriodId;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private LocalDate payDate;
    private int payrollStatusId;

    public PayPeriod(int payPeriodId, LocalDate periodStartDate, LocalDate periodEndDate,
                     LocalDate payDate, int payrollStatusId) {
        this.payPeriodId = payPeriodId;
        this.periodStartDate = periodStartDate;
        this.periodEndDate = periodEndDate;
        this.payDate = payDate;
        this.payrollStatusId = payrollStatusId;
    }

    public int getPayPeriodId() { return payPeriodId; }
    public LocalDate getPeriodStartDate() { return periodStartDate; }
    public LocalDate getPeriodEndDate() { return periodEndDate; }
    public LocalDate getPayDate() { return payDate; }
    public int getPayrollStatusId() { return payrollStatusId; }
}
