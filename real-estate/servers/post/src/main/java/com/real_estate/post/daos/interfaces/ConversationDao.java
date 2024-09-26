package com.real_estate.post.daos.interfaces;


import com.real_estate.post.models.ConversationEntity;

import java.util.List;
import java.util.Optional;

public interface ConversationDao {
    ConversationEntity saveConversation(ConversationEntity entity);

    Optional<ConversationEntity> findById(Long conversationId);

    List<ConversationEntity> findByMember(Long id, Long otherId);
}
