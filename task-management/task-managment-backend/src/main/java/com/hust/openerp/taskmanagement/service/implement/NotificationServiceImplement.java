package com.hust.openerp.taskmanagement.service.implement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.hust.openerp.taskmanagement.dto.request.NotificationRequest;
import com.hust.openerp.taskmanagement.service.NotificationService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImplement implements NotificationService {

    @Value("${notification-service.url}")
    private String notificationServiceUrl;

    private WebClient webClient;

    @PostConstruct
    public void initClient() {
        this.webClient = WebClient.builder().baseUrl(notificationServiceUrl)
                .build();
    }

    @Override
    public void sendNotification(String from, String to, String content, String url) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();
        String jwt = token.getToken().getTokenValue();
        webClient.post().uri("/notification").header("Authorization", "Bearer " + jwt)
                .bodyValue(new NotificationRequest(to, content, url)).retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse -> clientResponse.bodyToMono(String.class).map(RuntimeException::new))
                .bodyToMono(String.class).subscribe();
    }

}
