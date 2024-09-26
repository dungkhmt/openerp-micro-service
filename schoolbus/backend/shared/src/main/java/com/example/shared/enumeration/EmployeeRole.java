package com.example.shared.enumeration;

import lombok.Getter;

@Getter
public enum EmployeeRole {
    DRIVER("DRIVER"),
    DRIVER_MATE("DRIVER_MATE"),
    ;

    private final String value;

    EmployeeRole(String value) {
        this.value = value;
    }

}
