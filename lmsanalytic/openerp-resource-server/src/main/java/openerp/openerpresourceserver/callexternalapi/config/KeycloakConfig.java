package openerp.openerpresourceserver.callexternalapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class KeycloakConfig {
  @Value("${keycloaklms.realm}")
  private String realm;

  @Value("${keycloaklms.auth-server-url}")
  private String authServerUrl;

}
