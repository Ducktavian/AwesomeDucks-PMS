/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.motorph.model;

import java.time.LocalDate;

/**
 *
 * @author Lenovo
 */
public enum PayrollPeriod {
    
    FIRST_PERIOD("1st cutoff (1st-15th)"),
    SECOND_PERIOD("2nd cutoff (16th-end)");

    private final String label;

    PayrollPeriod(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
    
    public LocalDate getStartDate(int year, int month) {
        return (this == FIRST_PERIOD) 
            ? LocalDate.of(year, month, 1) 
            : LocalDate.of(year, month, 16);
    }

    public LocalDate getEndDate(int year, int month) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        return (this == FIRST_PERIOD) 
            ? LocalDate.of(year, month, 15) 
            : firstDay.withDayOfMonth(firstDay.lengthOfMonth());
    }
}
