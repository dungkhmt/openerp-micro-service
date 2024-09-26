package com.example.shared.enumeration;

public enum UserRole {
    ADMIN("admin"),
    CLIENT("client"),
    EMPLOYEE("employee");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getValue() {
        return role;
    }
}
