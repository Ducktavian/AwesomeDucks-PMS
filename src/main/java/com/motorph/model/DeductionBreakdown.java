package com.motorph.model;

public class DeductionBreakdown {

    // Monthly gross minus SSS/PhilHealth/Pag-IBIG - the base withholdingTax is computed from.
    // Does NOT include allowances (they are never part of the taxable base).
    private double taxableIncome;
    private double sss;
    private double philHealth;
    private double pagIbig;
    private double withholdingTax;

    public DeductionBreakdown(double taxableIncome, double sss, double philHealth, double pagIbig, double withholdingTax) {
        this.taxableIncome = taxableIncome;
        this.sss = sss;
        this.philHealth = philHealth;
        this.pagIbig = pagIbig;
        this.withholdingTax = withholdingTax;
    }

    public double getTaxableIncome() { return taxableIncome; }
    public double getSss() { return sss; }
    public double getPhilHealth() { return philHealth; }
    public double getPagIbig() { return pagIbig; }
    public double getWithholdingTax() { return withholdingTax; }
    public double getTotal() { return sss + philHealth + pagIbig + withholdingTax; }
}
