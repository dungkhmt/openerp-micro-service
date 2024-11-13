package com.hust.baseweb.applications.programmingcontest.callexternalapi.utils;

import java.time.Instant;

import org.springframework.stereotype.Component;

@Component
public class TokenStorage {
  private Token token;

  public void storeToken(String accessToken, Instant expiresAt) {
    token = new Token(accessToken, expiresAt);
  }

  public Token getToken() {
    return token;
  }

  public static class Token {
    private final String accessToken;
    private final Instant expiresAt;

    public Token(String accessToken, Instant expiresAt) {
      this.accessToken = accessToken;
      this.expiresAt = expiresAt;
    }

    public String getAccessToken() {
      return accessToken;
    }

    public Instant getExpiresAt() {
      return expiresAt;
    }

    public boolean isExpired() {
      return Instant.now().isAfter(expiresAt);
    }
  }
}
