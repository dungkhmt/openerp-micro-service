package com.real_estate.post.controllers;

import com.real_estate.post.dtos.InputTransportDTO;
import com.real_estate.post.dtos.OutputTransportDTO;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.models.MessageEntity;
import com.real_estate.post.services.AuthenticationService;
import com.real_estate.post.services.MessageService;
import com.real_estate.post.utils.TransportActionEnum;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("")
    @Operation(summary = "get 20 message by conversationId and last messageId", operationId = "meesage.getMessages")
    public ResponseEntity<ResponseDto<List<MessageEntity>>> get20MessageByConversationIdAndLastMessageId(
            @RequestParam("conversationId") Long conversationId,
            @RequestParam("lastMessageId") Long lastMessageId
    ) {
        List<MessageEntity> entities = messageService.getMessageBy(conversationId, lastMessageId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, entities));
    }


    @MessageMapping("/message")
    public void mainChannel(InputTransportDTO input) {
        TransportActionEnum action = input.getAction();
        switch (action) {
            case NOTIFICATION_MESSAGE :
                MessageEntity entity = messageService.getAndSave(input);
                OutputTransportDTO dto = new OutputTransportDTO();
                dto.setAction(TransportActionEnum.NOTIFICATION_MESSAGE);
                dto.setObject(entity);
                System.out.println("du lieu gui di" + dto);
                messagingTemplate.convertAndSendToUser(String.valueOf(input.getReceiverId()), "/user", dto);
                break;
            default:
                break;
        }
    }
}
