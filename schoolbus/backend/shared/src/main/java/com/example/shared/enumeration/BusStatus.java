package com.example.shared.enumeration;

public enum BusStatus {
    AVAILABLE("Available"),
    RUNNING("Running"),
    BROKEN("Broken"),
    MAINTENANCE("Maintenance");

    private final String status;

    BusStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return status;
    }
}
