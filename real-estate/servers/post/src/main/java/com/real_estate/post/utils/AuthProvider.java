package com.real_estate.post.utils;

public enum AuthProvider {
    local("local"),
    github("github"),
    facebook("facebook"),
    google("google");

    private final String text;

    AuthProvider(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
