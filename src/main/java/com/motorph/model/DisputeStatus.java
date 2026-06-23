package com.motorph.model;

public enum DisputeStatus {
    UNRESOLVED("UNRESOLVED"),
    RESOLVED("RESOLVED");

    private final String dbValue;

    DisputeStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static DisputeStatus fromDbValue(String value) {
        if (value == null) return UNRESOLVED;
        for (DisputeStatus s : values()) {
            if (s.dbValue.equalsIgnoreCase(value)) return s;
        }
        return UNRESOLVED;
    }
}
