package com.real_estate.post.dtos;

import com.real_estate.post.utils.MessageType;
import com.real_estate.post.utils.TransportActionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InputTransportDTO {

    private Long senderId;

    private Long receiverId;

    private TransportActionEnum action;

    private Long conversationId;

    private String content;

    private MessageType messageType;

    private Long messageId; // dung de sua xoa message
}
