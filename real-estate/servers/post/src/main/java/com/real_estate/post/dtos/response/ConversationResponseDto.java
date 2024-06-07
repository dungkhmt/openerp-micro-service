package com.real_estate.post.dtos.response;

import com.real_estate.post.models.MessageEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationResponseDto {
    Long conversationId;

    private boolean isLastMessage;

    Long lastTimeMessage;

    private AccountResponseDto other;

    private List<MessageEntity> messages;
}
