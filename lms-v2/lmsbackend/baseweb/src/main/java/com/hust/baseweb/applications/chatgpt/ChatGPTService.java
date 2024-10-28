package com.hust.baseweb.applications.chatgpt;

import com.hust.baseweb.applications.chatgpt.model.ChatGPTRequest;
import com.hust.baseweb.applications.chatgpt.model.ChatGPTResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatGPTService {
    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    public String getChatGPTAnswer(String prompt) {
        // create a request
        ChatGPTRequest request = new ChatGPTRequest(model, prompt);

        // call the API
        ChatGPTResponse response = restTemplate.postForObject(apiUrl, request, ChatGPTResponse.class);

//        ResponseEntity<ChatGPTResponse> responseEntity = restTemplate.exchange(
//            apiUrl,
//            HttpMethod.POST,
//            new HttpEntity<>(request, headers),
//            ChatGPTResponse.class
//        );
//
//        ChatGPTResponse response = responseEntity.getBody();

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "No response";
        }

        // return the first response
        return response.getChoices().get(0).getMessage().getContent();
    }
}
