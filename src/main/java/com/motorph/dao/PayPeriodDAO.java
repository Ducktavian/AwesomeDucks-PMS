package com.motorph.dao;

import com.motorph.model.PayPeriod;

import java.time.LocalDate;


public interface PayPeriodDAO {
    PayPeriod findOrCreate(LocalDate periodStart, LocalDate periodEnd, LocalDate payDate);

    // Pay period whose start/end range contains the given date, or null if none.
    PayPeriod findCurrent(LocalDate date);

    // Earliest pay period starting after the given date, or null if none.
    PayPeriod findUpcoming(LocalDate date);
}