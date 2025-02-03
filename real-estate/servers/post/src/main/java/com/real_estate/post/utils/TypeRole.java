package com.real_estate.post.utils;

public enum TypeRole {
    USER("USER"),
    ADMIN("ADMIN");

    private final String text;

    TypeRole(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
