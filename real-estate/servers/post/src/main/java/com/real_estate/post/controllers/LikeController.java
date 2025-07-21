package com.real_estate.post.controllers;

import com.real_estate.post.dtos.request.CreateLikeRequestDto;
import com.real_estate.post.dtos.response.AccountResponseDto;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.services.AuthenticationService;
import com.real_estate.post.services.LikeService;
import com.real_estate.post.utils.TypePost;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/like")
@RestController
public class LikeController {
    @Autowired
    LikeService likeService;

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("")
    @Operation(summary = "create like", operationId = "like.create")
    public ResponseEntity<ResponseDto<Long>> createLike(
            @RequestBody CreateLikeRequestDto dto
    ) {
        Long likerId = authenticationService.getAccountIdFromContext();
        Long likeId = likeService.createLike(likerId, dto.getPostId(), dto.getTypePost());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, likeId));
    }

    @DeleteMapping("")
    public ResponseEntity<ResponseDto<String>> deleteLike(
            @RequestParam(value = "likeId") Long likeId
    ) {
        Long likerId = authenticationService.getAccountIdFromContext();
        likeService.deleteLike(likeId, likerId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, "Hủy thành công"));
    }

    @GetMapping("")
    public ResponseEntity<ResponseDto<List<Object>>> getLikeByMe() {
        Long likerId = authenticationService.getAccountIdFromContext();
        List<Object> result = likeService.getLikeBy(likerId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, result));
    }

    @GetMapping("/liker")
    public ResponseEntity<ResponseDto<List<AccountResponseDto>>> getLiker(
            @RequestParam(value = "postId") Long postId,
            @RequestParam(value = "typePost") TypePost typePost
    ) {
        List<AccountResponseDto> dtos = likeService.getLiker(postId, typePost);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, dtos));
    }

    @GetMapping("/likeId")
    public ResponseEntity<ResponseDto<Long>> getLikeId(
            @RequestParam("postId") Long postId,
            @RequestParam("typePost") TypePost typePost
    ) {
        Long finderId = authenticationService.getAccountIdFromContext();
        Long likeId = likeService.getLikeId(postId, finderId, typePost);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, likeId));
    }
}
