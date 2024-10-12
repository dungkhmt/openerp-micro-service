package openerp.openerpresourceserver.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.config.ClientCredential;

@Service
@Log4j2
public class ApiService {
  private final WebClient webClient;
  private final KeycloakService keycloakService;
  private ClientCredential clientCredential;

  public ApiService(WebClient.Builder webClientBuilder, KeycloakService keycloakService,
      ClientCredential clientCredential) {
    // TODO: remove hard-coded URL
    this.webClient = webClientBuilder.baseUrl("https://analytics.soict.ai/api").build();
    this.keycloakService = keycloakService;
    this.clientCredential = clientCredential;
  }

  public <T> ResponseEntity<T> callApi(String endpoint, Class<T> responseType) {
    if (clientCredential == null) {
      throw new RuntimeException("Client credential is unset");
    }

    log.debug("Calling API with credential: {}, endpoint: {}", clientCredential, endpoint);
    String accessToken = keycloakService.getAccessToken(clientCredential.getClientId(),
        clientCredential.getClientSecret());
    log.debug("Get access token: " + accessToken);

    return this.webClient.get()
        .uri(endpoint)
        .header("Authorization", "Bearer " + accessToken)
        .retrieve()
        .toEntity(responseType)
        // .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))
        // .filter(RuntimeException.class::isInstance))
        .block();
  }

  public <T, B> ResponseEntity<T> callPostApi(String endpoint, Class<T> responseType, B body) {
    if (clientCredential == null) {
      throw new RuntimeException("Client credential is unset");
    }

    log.debug("Calling API with credential: {}, endpoint: {}", clientCredential, endpoint);
    String accessToken = keycloakService.getAccessToken(clientCredential.getClientId(),
        clientCredential.getClientSecret());
    log.debug("Get access token: " + accessToken);

    return this.webClient.post()
        .uri(endpoint)
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + accessToken)
        .body(BodyInserters.fromValue(body))
        .retrieve()
        .toEntity(responseType)
        // .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))
        // .filter(RuntimeException.class::isInstance))
        .block();
  }

  public <T> ResponseEntity<T> callApi(String endpoint, ParameterizedTypeReference<T> responseType) {
    if (clientCredential == null) {
      throw new RuntimeException("Client credential is unset");
    }

    log.debug("Calling API with credential: {}, endpoint: {}", clientCredential, endpoint);
    String accessToken = keycloakService.getAccessToken(clientCredential.getClientId(),
        clientCredential.getClientSecret());
    log.debug("Get access token: " + accessToken);

    return this.webClient.get()
        .uri(endpoint)
        .header("Authorization", "Bearer " + accessToken)
        .retrieve()
        .toEntity(responseType)
        // .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))
        // .filter(RuntimeException.class::isInstance))
        .block();
  }

  public void setCredential(ClientCredential clientCredential) {
    this.clientCredential = clientCredential;
  }

  public void setCredential(String clientId, String clientSecret) {
    this.clientCredential = new ClientCredential(clientId, clientSecret);
  }
}
