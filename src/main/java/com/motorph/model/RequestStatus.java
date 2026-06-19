package com.motorph.model;

/**
 * Maps to request_status.request_status_type values in the DB.
 */
public enum RequestStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED;

    /**
     * Convert from the DB string (e.g. "Pending" → PENDING).
     * Only the four statuses relevant to requests are included;
     * "Open" / "Resolved" belong to help_center_ticket, not requests.
     */
    public static RequestStatus fromDbValue(String dbValue) {
        return switch (dbValue) {
            case "Pending"   -> PENDING;
            case "Approved"  -> APPROVED;
            case "Rejected"  -> REJECTED;
            case "Cancelled" -> CANCELLED;
            default -> throw new IllegalArgumentException("Unknown request status: " + dbValue);
        };
    }

    // Returns the capitalised string stored in request_status. */
    public String toDbValue() {
        String lower = name().toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}