package com.real_estate.post.utils;

public enum TypePost {
    BUY("BUY"),
    SELL("SELL");

    private final String text;

    TypePost(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
