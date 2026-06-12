
package com.motorph.model;


public enum Role {
    HR("HR"),
    FINANCE("Finance"),
    IT("IT"),
    EMPLOYEE("Employee"),
    ADMIN("Admin");
    
    private String label;
    
    Role(String label) {
        this.label = label;
    }
    
    @Override
    public String toString() {
        return label;
    }
    
    
}
