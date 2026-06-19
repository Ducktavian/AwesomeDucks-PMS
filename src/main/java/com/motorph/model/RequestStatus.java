package com.motorph.model;

/**
 *
 * @author Ducktavian
 */
public enum RequestStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED;

    public static RequestStatus fromDbValue(String dbValue) {
        return switch (dbValue) {
            case "Pending"   -> PENDING;
            case "Approved"  -> APPROVED;
            case "Rejected"  -> REJECTED;
            case "Cancelled" -> CANCELLED;
            default -> throw new IllegalArgumentException("Unknown request status: " + dbValue);
        };
    }

    // Returns the capitalised string stored in request_status.
    public String toDbValue() {
        String lower = name().toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}