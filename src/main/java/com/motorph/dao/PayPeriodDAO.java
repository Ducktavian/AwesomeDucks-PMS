package com.motorph.dao;

import com.motorph.model.PayPeriod;

import java.time.LocalDate;


public interface PayPeriodDAO {
    PayPeriod findOrCreate(LocalDate periodStart, LocalDate periodEnd, LocalDate payDate);
}