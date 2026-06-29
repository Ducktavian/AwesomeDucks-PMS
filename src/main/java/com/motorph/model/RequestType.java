package com.motorph.model;

/**
 *
 * @author Ducktavian
 */
public enum RequestType {
    LEAVE,
    OVERTIME,
    UNDERTIME;

    public static RequestType fromDbValue(String dbValue) {
        return switch (dbValue.toLowerCase()) {
            case "leave"  -> LEAVE;
            case "overtime"  -> OVERTIME;
            case "undertime" -> UNDERTIME;
            default -> throw new IllegalArgumentException("Unknown request_type: " + dbValue);
        };
    }

    // Returns the lowercase string stored in work_time_request.request_type. 
    public String toDbValue() {
        return name().toLowerCase();
    }
}