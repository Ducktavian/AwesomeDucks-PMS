
package com.motorph.model;


public enum EmploymentStatus {
    
    REGULAR("Regular"),
    PROBATIONARY("Probationary");
    
    private final String label;
    
    EmploymentStatus(String label) {
        this.label = label;
    }
    
    @Override 
    public String toString() {
        return label;
    }
    
}
