package com.example.shared.enumeration;

public enum RideStatus {
    PENDING("PENDING"),
    READY("READY"),
    RUNNING("RUNNING"),
    AT_SCHOOL("AT_SCHOOL"),
    FINISHED("FINISHED");


    private final String status;

    RideStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return status;
    }
}
