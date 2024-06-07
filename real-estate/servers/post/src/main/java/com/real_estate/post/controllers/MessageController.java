package com.real_estate.post.controllers;

import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.models.MessageEntity;
import com.real_estate.post.services.AuthenticationService;
import com.real_estate.post.services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/message")
@RestController
public class MessageController {
    @Autowired
    MessageService messageService;

    @Autowired
    AuthenticationService authenticationService;

    @GetMapping("")
    @Operation(summary = "get 20 message by conversationId and last messageId", operationId = "meesage.getMessages")
    public ResponseEntity<ResponseDto<List<MessageEntity>>> get20MessageByConversationIdAndLastMessageId(
            @RequestParam("conversationId") Long conversationId,
            @RequestParam("lastMessageId") Long lastMessageId
    ) {
        List<MessageEntity> entities = messageService.getMessageBy(conversationId, lastMessageId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, entities));
    }
}
