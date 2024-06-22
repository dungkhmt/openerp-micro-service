package com.real_estate.post.controllers;

import com.real_estate.post.dtos.OutputTransportDTO;
import com.real_estate.post.dtos.request.FirstMessageRequestDto;
import com.real_estate.post.dtos.response.ConversationResponseDto;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.services.AuthenticationService;
import com.real_estate.post.services.ConversationService;
import com.real_estate.post.utils.TransportActionEnum;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/conversation")
@RestController
public class ConversationController {
    @Autowired
    ConversationService conversationService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("")
    @Operation(summary = "create conversation from first message", operationId = "conversation.create")
    public ResponseEntity<ResponseDto<ConversationResponseDto>> createFirstMessage(
            @RequestBody FirstMessageRequestDto requestDto
    ) {
        Long myId = authenticationService.getAccountIdFromContext();
        ConversationResponseDto result = conversationService.createConversationAndMessage(requestDto, myId);

        OutputTransportDTO dto = new OutputTransportDTO();
        dto.setAction(TransportActionEnum.NOTIFICATION_MESSAGE);
        dto.setObject(result.getMessages().get(0));
        messagingTemplate.convertAndSendToUser(String.valueOf(requestDto.getReceiverId()), "/user", dto);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, result));
    }

    @GetMapping()
    @Operation(summary = "get conversation by myId and otherId", operationId = "conversation.getConversation")
    public ResponseEntity<ResponseDto<ConversationResponseDto>> getConversationByMyIdAndOtherId(
            @RequestParam("otherId") Long otherId
    ) {
        Long myId = authenticationService.getAccountIdFromContext();
        ConversationResponseDto result = conversationService.getConversationByAllMemberId(myId, otherId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, result));
    }

    @GetMapping("/all")
    @Operation(summary = "get all my conversations", operationId = "conversation.getAll")
    public ResponseEntity<ResponseDto<List<ConversationResponseDto>>> getAll() {
        Long myId = authenticationService.getAccountIdFromContext();
        List<ConversationResponseDto> dtos = conversationService.getAll(myId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, dtos));
    }
}
