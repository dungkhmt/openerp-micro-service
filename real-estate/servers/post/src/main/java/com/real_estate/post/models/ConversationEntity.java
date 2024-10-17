package com.real_estate.post.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ConversationEntity {
    private Long conversationId;

    private Set<Long> memberIds;

    private Long createdAt;

    private Long updatedAt;
}
