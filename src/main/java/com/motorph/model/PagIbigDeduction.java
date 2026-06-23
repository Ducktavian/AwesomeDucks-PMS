package com.motorph.model;

public class PagIbigDeduction implements DeductionRule {

    @Override
    public double calculate(double grossPay) {
        double contribution = (grossPay >= 1000 && grossPay <= 1500)
                ? grossPay * 0.01
                : grossPay * 0.02;
        return Math.min(contribution, 100.00);
    }
}
