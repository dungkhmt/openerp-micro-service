package com.real_estate.post.dtos.request;

import com.real_estate.post.utils.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FirstMessageRequestDto {
    Long receiverId;
    String content;
    MessageType messageType;
}
