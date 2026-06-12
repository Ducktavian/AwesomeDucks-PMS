package com.motorph.service;

import com.motorph.model.DeductionRule;
import com.motorph.model.PagIbigDeduction;
import com.motorph.model.PhilHealthDeduction;
import com.motorph.model.SSSDeduction;
import com.motorph.model.TaxDeduction;


public class DeductionService {
    
        private final DeductionRule sssRule = new SSSDeduction();
        private final DeductionRule philHealthRule = new PhilHealthDeduction();
        private final DeductionRule pagIbigRule = new PagIbigDeduction();
        private final TaxDeduction taxRule = new TaxDeduction();
    
        
    public DeductionService() {
    
    }
    
    public double calculateTotalContributions(double monthlyGross) {
        
        // Monthly contributions
        double monthlySSS = calculateSSSContribution(monthlyGross);
        double monthlyPhilHealth = calculatePhilHealthContribution(monthlyGross);
        double monthlyPagIbig = calculatePagIbigContribution(monthlyGross);
        
        return monthlySSS + monthlyPhilHealth + monthlyPagIbig;
        
    }
    
    public double calculateSSSContribution(double grossPay) {
        return sssRule.calculate(grossPay);
    }
    
    public double calculatePhilHealthContribution(double grossPay) {
        return philHealthRule.calculate(grossPay);
    }
    
    public double calculatePagIbigContribution(double grossPay) {
        return pagIbigRule.calculate(grossPay);
    }
    
    public double calculateTax(double grossPay) {
        return taxRule.calculate(grossPay);
    }

}
