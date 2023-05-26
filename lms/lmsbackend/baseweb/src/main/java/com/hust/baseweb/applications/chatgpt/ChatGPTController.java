package com.hust.baseweb.applications.chatgpt;

import com.hust.baseweb.applications.chatgpt.model.ChatGPTRequest;
import com.hust.baseweb.applications.chatgpt.model.ChatGPTResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@CrossOrigin
public class ChatGPTController {
    @Autowired
    private ChatGPTService chatGPTService;

    @GetMapping("/test-chat-gpt")
    public String chat(@RequestParam String prompt) {
        return chatGPTService.getChatGPTAnswer(prompt);
    }
}
