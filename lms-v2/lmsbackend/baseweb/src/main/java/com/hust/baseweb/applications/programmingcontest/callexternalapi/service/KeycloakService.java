package com.hust.baseweb.applications.programmingcontest.callexternalapi.service;

import com.hust.baseweb.applications.programmingcontest.callexternalapi.config.KeycloakConfig;
import com.hust.baseweb.applications.programmingcontest.callexternalapi.utils.TokenStorage;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.log4j.Log4j2;


import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Log4j2
public class KeycloakService {
  private final WebClient webClient;
  private final TokenStorage tokenStorage;
  private final KeycloakConfig keycloakConfig;

  public KeycloakService(WebClient.Builder webClientBuilder, TokenStorage tokenStorage, KeycloakConfig keycloakConfig) {
    this.tokenStorage = tokenStorage;
    this.keycloakConfig = keycloakConfig;
    this.webClient = webClientBuilder.baseUrl(keycloakConfig.getAuthServerUrl()).build();
  }

  public String getAccessToken(String clientId, String clientSecret) {
    TokenStorage.Token token = tokenStorage.getToken();
    if (token != null && !token.isExpired()) {
      log.debug("[Cache hit] Token is not expired");
      return token.getAccessToken();
    }

    log.debug("[Cache miss] Token is miss or expired");
    return requestNewAccessToken(clientId, clientSecret);
  }

  private String requestNewAccessToken(String clientId, String clientSecret) {
    String tokenUrl = UriComponentsBuilder
        .fromPath("/realms/" + keycloakConfig.getRealm() + "/protocol/openid-connect/token")
        .toUriString();

    String response = this.webClient.post()
        .uri(tokenUrl)
        .header("Content-Type", "application/x-www-form-urlencoded")
        .bodyValue("client_id=" + clientId +
            "&client_secret=" + clientSecret +
            "&grant_type=client_credentials")
        .retrieve()
        .bodyToMono(String.class)
        .block();

    String accessToken = extractAccessToken(response);
    long expiresIn = extractExpiresIn(response);

    tokenStorage.storeToken(accessToken, Instant.now().plus(expiresIn, ChronoUnit.SECONDS));

    return accessToken;
  }

  private String extractAccessToken(String responseBody) {
    return responseBody.split("\"access_token\":\"")[1].split("\"")[0];
  }

  private long extractExpiresIn(String responseBody) {
    return Long.parseLong(responseBody.split("\"expires_in\":")[1].split(",")[0]);
  }
}
