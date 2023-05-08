package com.hust.wmsbackend.management.utils;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExternalApiCaller {

    public boolean simpleBooleanCall (String uri, String token, String requestJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.set("Content-Type", "application/json;charset=UTF-8");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        ResponseEntity<Boolean> response = restTemplate.postForEntity(uri, entity, Boolean.class);
        return response.getStatusCode() == HttpStatus.OK;
    }

}
