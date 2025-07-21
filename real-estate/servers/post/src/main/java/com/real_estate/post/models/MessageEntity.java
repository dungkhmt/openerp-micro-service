package com.real_estate.post.models;

import com.real_estate.post.utils.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MessageEntity {
    private Long messageId;

    private Long senderId;

    private Long conversationId;

    private String content;

    private MessageType messageType;

    private Long createdAt;

    private Long updatedAt;
}
