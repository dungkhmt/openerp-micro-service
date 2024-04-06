package com.example.shared.enumeration;

public enum UserRole {
    ADMIN("admin"),
    CLIENT("client");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getValue() {
        return role;
    }
}
