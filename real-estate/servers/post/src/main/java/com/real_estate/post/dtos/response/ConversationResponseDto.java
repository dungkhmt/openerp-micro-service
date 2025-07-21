package com.real_estate.post.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.real_estate.post.models.MessageEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationResponseDto {
    private Long conversationId;

    @JsonProperty("isLastMessage")
    private boolean isLastMessage;

    private Long lastTimeMessage;

    private AccountResponseDto other;

    private List<MessageEntity> messages;
}
