package com.example.shared.enumeration;

public enum AuthProvider {
    GOOGLE("google");

    private final String provider;

    AuthProvider(String provider) {
        this.provider = provider;
    }

    public String getValue() {
        return provider;
    }

    public static AuthProvider fromValue(String value) {
        for (AuthProvider provider : AuthProvider.values()) {
            if (provider.provider.equalsIgnoreCase(value)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Invalid AuthProvider value: " + value);
    }
}
