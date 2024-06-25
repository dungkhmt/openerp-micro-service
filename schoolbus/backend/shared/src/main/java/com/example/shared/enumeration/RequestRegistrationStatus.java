package com.example.shared.enumeration;

public enum RequestRegistrationStatus {
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED"),;

    private final String status;

    RequestRegistrationStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return status;
    }
}
