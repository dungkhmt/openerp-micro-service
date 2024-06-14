package com.real_estate.post.controllers;

import com.real_estate.post.dtos.request.SaveRequestDto;
import com.real_estate.post.dtos.response.AccountResponseDto;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.services.AuthenticationService;
import com.real_estate.post.services.SaveService;
import com.real_estate.post.utils.TypePost;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/save")
@RestController
public class SaveController {
    @Autowired
    SaveService saveService;

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("")
    @Operation(summary = "create save post", operationId = "save.create")
    public ResponseEntity<ResponseDto<Long>> createSave(
            @RequestBody SaveRequestDto dto
    ) {
        Long accountId = authenticationService.getAccountIdFromContext();
        Long saveId = saveService.createSave(accountId, dto.getPostId(), dto.getTypePost());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, saveId));
    }

    @DeleteMapping("")
    public ResponseEntity<ResponseDto<String>> deleteSave(
            @RequestParam(value = "saveId") Long saveId
    ) {
        Long accountId = authenticationService.getAccountIdFromContext();
        saveService.deleteSave(saveId, accountId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, "Hủy thành công"));
    }

    @GetMapping("")
    public ResponseEntity<ResponseDto<List<Object>>> getSaveByMe() {
        Long saverId = authenticationService.getAccountIdFromContext();
        List<Object> result = saveService.getSaveBy(saverId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, result));
    }

    @GetMapping("/saver")
    public ResponseEntity<ResponseDto<List<AccountResponseDto>>> getSaverOfPost(
            @RequestParam(value = "postId") Long postId,
            @RequestParam(value = "typePost") TypePost typePost
    ) {
        List<AccountResponseDto> dtos = saveService.getSaverOfPost(postId, typePost);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, dtos));
    }
}
