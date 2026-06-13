/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motorph.model;

/**
 *
 * @author Lenovo
 */
public class PhilHealthDeduction implements DeductionRule {
    
    public PhilHealthDeduction () {
    }
    
    @Override
    public double calculate(double grossPay) {
        double salaryBase = Math.max(10000, Math.min(grossPay, 60000));
        return (salaryBase * 0.03) / 2;
    }
    
}
