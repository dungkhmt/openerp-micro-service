package com.real_estate.post.utils;

public enum LegalDocument {
    HAVE("HAVE"),
    WAIT("WAIT"),
    HAVE_NOT("HAVE_NOT");


    private final String text;

    LegalDocument(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static LegalDocument fromText(String text) {
        for (LegalDocument document : LegalDocument.values()) {
            if (document.text.equalsIgnoreCase(text)) {
                return document;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
