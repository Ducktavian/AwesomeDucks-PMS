/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.model;

/**
 *
 * @author Lenovo
 */
public class TaxDeduction implements DeductionRule{

    
    
    // WithHoldingTax
    @Override
    public double calculate(double taxableIncome) {
        
        if (taxableIncome <= 20832) {
            return 0;
        } 
        else if (taxableIncome < 33333) {
            return (taxableIncome - 20833) * 0.20;
        }
        else if (taxableIncome < 66667) {
            return 2500 + (taxableIncome - 33333) * 0.25;
        }
        else if (taxableIncome < 166667) {
            return 10833 + (taxableIncome - 66667) * 0.30;
        }
        else if (taxableIncome < 666667) {
            return 40833.33 + (taxableIncome - 166667) * 0.32;
        }
        else {
            return 200833.33 + (taxableIncome - 666667) * 0.35;
        }
    }
}
