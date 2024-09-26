package com.example.shared.enumeration;

public enum StudentPickupPointStatus {
    PICKING("PICKING"),
    PICKED("PICKED"),
    MISSED("MISSED"),
    AT_SCHOOL("AT_SCHOOL"),
    AT_HOME("AT_HOME");

    private final String status;

    StudentPickupPointStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return status;
    }
}
