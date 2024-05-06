package com.real_estate.post.utils;

public enum LegalDocuments {
    HAVE("HAVE"),
    WAIT("WAIT"),
    HAVE_NOT("HAVE_NOT");


    private final String text;

    LegalDocuments(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static LegalDocuments fromText(String text) {
        for (LegalDocuments document : LegalDocuments.values()) {
            if (document.text.equalsIgnoreCase(text)) {
                return document;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
