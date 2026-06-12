
package com.motorph.model;

/**
 *
 * @author Lenovo
 */
public class DeductionBreakdown {
    
    private double sss;
    private double philHealth;
    private double pagIbig;
    private double withholdingTax;

    public DeductionBreakdown(double sss, double philHealth, double pagIbig, double withholdingTax) {
        this.sss = sss;
        this.philHealth = philHealth;
        this.pagIbig = pagIbig;
        this.withholdingTax = withholdingTax;
    }

    // GETTERS
    public double getSss() {
        return sss;
    }

    public double getPhilHealth() {
        return philHealth;
    }

    public double getPagIbig() {
        return pagIbig;
    }

    public double getWithholdingTax() {
        return withholdingTax;
    }
   
    public double getTotal() {
        return sss + philHealth + pagIbig + withholdingTax;
    }

       
    
    
}
