package com.real_estate.post.controllers;

import com.real_estate.post.dtos.InputTransportDTO;
import com.real_estate.post.dtos.OutputTransportDTO;
import com.real_estate.post.models.MessageEntity;
import com.real_estate.post.services.MessageService;
import com.real_estate.post.utils.TransportActionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WsController  {
    @Autowired
    MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/message")
    public void mainChannel(InputTransportDTO input) {
        System.out.println("du liejeu nhan " +input.getContent());
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
