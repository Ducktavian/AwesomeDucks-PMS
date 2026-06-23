package com.motorph.model;

public class PhilHealthDeduction implements DeductionRule {

    @Override
    public double calculate(double grossPay) {
        double salaryBase = Math.max(10000, Math.min(grossPay, 60000));
        return (salaryBase * 0.03) / 2;
    }
}
