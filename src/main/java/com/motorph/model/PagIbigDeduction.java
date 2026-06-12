
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
        
        if (grossPay >= 1000 && grossPay <= 1500) {
            return grossPay * 0.01;
        } else {
            return grossPay * 0.02;
        }
        
    }
}
