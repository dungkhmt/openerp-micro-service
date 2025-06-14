package com.real_estate.post.daos.interfaces;

import com.real_estate.post.models.MessageEntity;

import java.util.List;

public interface MessageDao {
    MessageEntity saveMessage(MessageEntity entity);

    List<MessageEntity> findLast20By(Long conversationId, Long messageId);
}
