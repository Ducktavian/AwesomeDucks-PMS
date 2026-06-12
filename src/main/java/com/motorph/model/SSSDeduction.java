
package com.motorph.model;

import java.util.TreeMap;
import java.util.Map;
/**
 *
 * @author Lenovo
 */
public class SSSDeduction implements DeductionRule {
    
    private static final TreeMap<Double, Double> SSS_TABLE = new TreeMap<>();
    
    static {
        SSS_TABLE.put(0.0, 135.00);
        SSS_TABLE.put(3250.0, 157.50);
        SSS_TABLE.put(3750.0, 180.00);
        SSS_TABLE.put(4250.0, 202.50);
        SSS_TABLE.put(4750.0, 225.00);
        SSS_TABLE.put(5250.0, 147.50);
        SSS_TABLE.put(5750.0, 270.00);
        SSS_TABLE.put(6250.0, 292.50);
        SSS_TABLE.put(6750.0, 315.00);
        SSS_TABLE.put(7250.0, 337.50);
        SSS_TABLE.put(7750.0, 360.00);
        SSS_TABLE.put(8250.0, 382.50);
        SSS_TABLE.put(8750.0, 405.00);
        SSS_TABLE.put(9250.0, 427.50);
        SSS_TABLE.put(9750.0, 450.00);
        SSS_TABLE.put(10250.0, 472.50);
        SSS_TABLE.put(10750.0, 495.00);
        SSS_TABLE.put(11250.0, 517.50);
        SSS_TABLE.put(11750.0, 540.00);
        SSS_TABLE.put(12250.0, 562.50);
        SSS_TABLE.put(12750.0, 585.00);
        SSS_TABLE.put(13250.0, 607.50);
        SSS_TABLE.put(13750.0, 630.00);
        SSS_TABLE.put(14250.0, 652.50);
        SSS_TABLE.put(14750.0, 675.00);
        SSS_TABLE.put(15250.0, 697.50);
        SSS_TABLE.put(15750.0, 720.00);
        SSS_TABLE.put(16250.0, 742.50);
        SSS_TABLE.put(16750.0, 765.00);
        SSS_TABLE.put(17250.0, 787.50);
        SSS_TABLE.put(17750.0, 810.00);
        SSS_TABLE.put(18250.0, 832.50);
        SSS_TABLE.put(18750.0, 855.00);
        SSS_TABLE.put(19250.0, 877.50);
        SSS_TABLE.put(19750.0, 900.00);
        SSS_TABLE.put(20250.0, 922.50);
        SSS_TABLE.put(20750.0, 945.00);
        SSS_TABLE.put(21250.0, 967.50);
        SSS_TABLE.put(21750.0, 990.00);
        SSS_TABLE.put(22250.0, 1012.50);
        SSS_TABLE.put(22750.0, 1035.00);
        SSS_TABLE.put(23250.0, 1057.50);
        SSS_TABLE.put(23750.0, 1080.00);
        SSS_TABLE.put(24250.0, 1102.50);
        SSS_TABLE.put(24750.0, 1125.00);
    }
    
    public SSSDeduction() {
        
    }
    
    @Override
    public double calculate(double grossPay) {
        
        Map.Entry<Double, Double> entry = SSS_TABLE.floorEntry(grossPay);
        
        return (entry != null) ? entry.getValue() : 135.00;
    }
       
}
