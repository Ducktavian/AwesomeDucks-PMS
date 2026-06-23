
package com.motorph.model;

/**
 *
 * @author Lenovo
 */
public class PagIbigDeduction implements DeductionRule {
    
    public PagIbigDeduction() {
        
    }
    
    // PagIBig
    public double calculate(double grossPay) {
        double contribution;
        if (grossPay >= 1000 && grossPay <= 1500) {
            contribution = grossPay * 0.01;
        } else {
            contribution = grossPay * 0.02;
        }
        return Math.min(contribution, 100.00);
    }
}
