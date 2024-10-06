package com.hust.baseweb.applications.programmingcontest.callexternalapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Configuration
public class ClientCredential {
  @Value("${keycloak.credentials.client-id}")
  private String clientId;
  @Value("${keycloak.credentials.client-secret}")
  private String clientSecret;

}
