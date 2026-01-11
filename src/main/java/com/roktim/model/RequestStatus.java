package com.roktim.model;

public enum RequestStatus {
    PENDING("Pending"),
    FULFILLED("Fulfilled"),
    CANCELLED("Cancelled");

    private final String displayName;

    RequestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static RequestStatus fromString(String text) {
        for (RequestStatus status : RequestStatus.values()) {
            if (status.displayName.equalsIgnoreCase(text) || status.name().equalsIgnoreCase(text)) {
                return status;
            }
        }
        return PENDING; // default
    }
}
