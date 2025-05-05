package com.real_estate.post.utils;

public enum TypeLegalDocument {
    HAVE("HAVE"),
    WAIT("WAIT"),
    HAVE_NOT("HAVE_NOT");


    private final String text;

    TypeLegalDocument(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static TypeLegalDocument fromText(String text) {
        for (TypeLegalDocument document : TypeLegalDocument.values()) {
            if (document.text.equalsIgnoreCase(text)) {
                return document;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
