package com.hust.baseweb.applications.programmingcontest.callexternalapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class KeycloakConfig {
  @Value("${keycloak.realm}")
  private String realm;

  @Value("${keycloak.auth-server-url}")
  private String authServerUrl;

}
