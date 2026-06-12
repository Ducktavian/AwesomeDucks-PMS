
package com.motorph.model;


public enum LeaveType {

    SICK("Sick Leave"),
    VACATION("Vacation Leave"),
    MATERNITY("Maternity Leave");

    private final String label;

    LeaveType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}