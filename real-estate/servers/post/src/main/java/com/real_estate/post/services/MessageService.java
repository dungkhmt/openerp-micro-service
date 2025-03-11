package com.real_estate.post.services;

import com.real_estate.post.daos.interfaces.MessageDao;
import com.real_estate.post.dtos.InputTransportDTO;
import com.real_estate.post.models.MessageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    @Qualifier("messageImpl")
    MessageDao messageDao;

    public List<MessageEntity> getMessageBy(Long conversationId, Long lastMessId) {
        return messageDao.findLast20By(conversationId, lastMessId);
    }

    public MessageEntity getAndSave(InputTransportDTO input) {
        Long now = System.currentTimeMillis();
        MessageEntity entity = new MessageEntity().builder()
                .senderId(input.getSenderId())
                .conversationId(input.getConversationId())
                .content(input.getContent())
                .messageType(input.getMessageType())
                .createdAt(now)
                .updatedAt(now)
                .build();
        entity = messageDao.saveMessage(entity);
        return entity;
    }
}
