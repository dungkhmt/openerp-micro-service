package com.example.shared.enumeration;

public enum RidePickupPointStatus {
    PICKING("PICKING"),
    PICKED("PICKED");

    private final String status;

    RidePickupPointStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return status;
    }
}
