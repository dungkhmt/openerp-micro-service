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
  //@Value("${keycloak.credentials.client-id}")
  private String clientId = "hustack";
  //@Value("${keycloak.credentials.client-secret}")
  //private String clientSecret = "zckLs48DHEXH2VXP4bWZyE9Ts0q53reZ";
  private String clientSecret = "MBt7h6rHtROccQ0GpGeeeo4s1dHROEzu";

}
