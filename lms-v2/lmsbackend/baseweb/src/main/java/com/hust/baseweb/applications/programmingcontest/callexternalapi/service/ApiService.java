package com.hust.baseweb.applications.programmingcontest.callexternalapi.service;

import com.hust.baseweb.applications.programmingcontest.callexternalapi.config.ClientCredential;
import com.hust.baseweb.applications.programmingcontest.callexternalapi.model.LmsLogModelCreate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.log4j.Log4j2;
//import openerp.openerpresourceserver.config.ClientCredential;

@Service
@Log4j2
public class ApiService {
  private final WebClient webClient;
  private final KeycloakService keycloakService;
  private ClientCredential clientCredential;

  public ApiService(WebClient.Builder webClientBuilder, KeycloakService keycloakService,
      ClientCredential clientCredential) {
    // TODO: remove hard-coded URL
    //this.webClient = webClientBuilder.baseUrl("http://localhost:8081/api").build();
      //this.webClient = webClientBuilder.baseUrl("http://localhost:9090/api").build();
      this.webClient = webClientBuilder.baseUrl("https://analytics.soict.ai").build();

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

    try {
        return this.webClient.get()
                             .uri(endpoint)
                             .header("Authorization", "Bearer " + accessToken)
                             .retrieve()
                             .toEntity(responseType)
                             // .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))
                             // .filter(RuntimeException.class::isInstance))
                             .block();
    }catch (Exception e){
        e.printStackTrace();
    }
    return null;
  }
    public <T, B> ResponseEntity<T> callPostApi(String endpoint, Class<T> responseType, B body) {
        if (clientCredential == null) {
            throw new RuntimeException("Client credential is unset");
        }
        log.debug("Calling API with credential: {}, endpoint: {}", clientCredential, endpoint);
        String accessToken = keycloakService.getAccessToken(clientCredential.getClientId(),
                                                            clientCredential.getClientSecret());
        log.debug("Get access token: " + accessToken);

        try {
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
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

  public ResponseEntity<?> callLogAPI(String url, LmsLogModelCreate model){
      if (clientCredential == null) {
          throw new RuntimeException("Client credential is unset");
      }

      log.debug("Calling API with credential: {}, endpoint: {}", clientCredential.getClientId() + "," + clientCredential.getClientSecret(), url);
      String accessToken = keycloakService.getAccessToken(clientCredential.getClientId(),
                                                          clientCredential.getClientSecret());
      log.debug("Get access token: " + accessToken);

      try {
          return this.webClient.post()
                               .uri(url)
                               .contentType(MediaType.APPLICATION_JSON)
                               .header("Authorization", "Bearer " + accessToken)
                               .body(BodyInserters.fromValue(model))
                               .retrieve()
                               //.toEntity(responseType)
                               .toEntity(Void.class)
                               // .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))
                               // .filter(RuntimeException.class::isInstance))
                               .block();
      }catch (Exception e){
          e.printStackTrace();
      }
       return null;

      /*
      this.webClient.post()
                           //.uri(url + "/log/create-log")
                    //.uri("/log/create-log")
                    .uri(url)
          .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(BodyInserters.fromValue(model))
                           .header("Authorization", "Bearer " + accessToken)
                           .retrieve()
          .toEntity(LmsLogModelCreate.class)
          .subscribe(
            responseEntity ->{
                HttpStatus status = responseEntity.getStatusCode();
                // Handle success response here
                //HttpStatusCode status = responseEntity.getStatusCode();
                //URI location = responseEntity.getHeaders().getLocation();
                //Employee createdEmployee = responseEntity.getBody();    // Response body
                // handle response as necessary
                log.info("callLogAPI -> OK!!!");
            },
            error -> {
                //HttpStatus status = responseEntity.getStatusCode();
                log.info("callLogAPI -> ERROR ??? status = " + error.getMessage());
            }
          );


      return ResponseEntity.ok().body("OK");
      */
  }
  public <T> ResponseEntity<T> callApi(String endpoint, ParameterizedTypeReference<T> responseType) {
    if (clientCredential == null) {
      throw new RuntimeException("Client credential is unset");
    }

    log.debug("Calling API with credential: {}, endpoint: {}", clientCredential, endpoint);
    String accessToken = keycloakService.getAccessToken(clientCredential.getClientId(),
        clientCredential.getClientSecret());
    log.debug("Get access token: " + accessToken);

    try {
        return this.webClient.get()
                             .uri(endpoint)
                             .header("Authorization", "Bearer " + accessToken)
                             .retrieve()
                             .toEntity(responseType)
                             // .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))
                             // .filter(RuntimeException.class::isInstance))
                             .block();
    }catch (Exception e){
        e.printStackTrace();
    }
    return null;
  }

  public void setCredential(ClientCredential clientCredential) {
    this.clientCredential = clientCredential;
  }

  public void setCredential(String clientId, String clientSecret) {
    this.clientCredential = new ClientCredential(clientId, clientSecret);
  }
}
