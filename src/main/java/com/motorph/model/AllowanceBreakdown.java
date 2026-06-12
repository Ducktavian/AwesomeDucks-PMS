package com.motorph.model;


public class AllowanceBreakdown {
    
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    
    public AllowanceBreakdown(double riceSubsidy, double phoneAllowance, double clothingAllowance) {
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
    }
    
    public double getRiceSubsidy() { 
        return riceSubsidy; 
    }
    
    public double getPhoneAllowance() {
        return phoneAllowance;
    }
    
    public double getClothingAllowance() {
        return clothingAllowance;
    }
    
    public double getTotal() {
        return riceSubsidy + phoneAllowance + clothingAllowance;
    }
}
