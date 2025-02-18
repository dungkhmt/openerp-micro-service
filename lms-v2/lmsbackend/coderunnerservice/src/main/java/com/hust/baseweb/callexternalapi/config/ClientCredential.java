package com.hust.baseweb.callexternalapi.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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
