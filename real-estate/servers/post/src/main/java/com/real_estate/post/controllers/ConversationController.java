package com.real_estate.post.controllers;

import com.real_estate.post.dtos.request.FirstMessageRequestDto;
import com.real_estate.post.dtos.response.ConversationResponseDto;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.services.AuthenticationService;
import com.real_estate.post.services.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/conversation")
@RestController
public class ConversationController {
    @Autowired
    ConversationService conversationService;

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("")
    @Operation(summary = "create conversation from first message", operationId = "conversation.create")
    public ResponseEntity<ResponseDto<ConversationResponseDto>> createFirstMessage(
            @RequestBody FirstMessageRequestDto requestDto
    ) {
        Long myId = authenticationService.getAccountIdFromContext();
        ConversationResponseDto result = conversationService.createConversationAndMessage(requestDto, myId);
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
