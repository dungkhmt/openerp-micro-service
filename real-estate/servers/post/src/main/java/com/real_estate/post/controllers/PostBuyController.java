package com.real_estate.post.controllers;

import com.real_estate.post.dtos.request.CreatePostBuyRequestDto;
import com.real_estate.post.dtos.request.UpdatePostBuyRequestDto;
import com.real_estate.post.dtos.request.UpdateStatusPost;
import com.real_estate.post.dtos.response.PostSellResponseDto;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.models.PostBuyEntity;
import com.real_estate.post.services.AuthenticationService;
import com.real_estate.post.services.PostBuyService;
import com.real_estate.post.services.PostSellService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/post/buy")
public class PostBuyController {
	@Autowired
	AuthenticationService authenticationService;

	@Autowired
	private PostBuyService postBuyService;

	@PostMapping()
	@Operation(summary = "create a new post buy", operationId = "buy.createPost")
	public ResponseEntity<ResponseDto<String>> createPostBuy(@Valid @RequestBody CreatePostBuyRequestDto requestDto) {
		Long accountId = authenticationService.getAccountIdFromContext();
		postBuyService.createPostBuy(requestDto, accountId);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, "Đăng tin mua thành công"));
	}

	@PutMapping("/updateStatus")
	@Operation(operationId = "postbuy.updateStatus", summary = "update status of post")
	public ResponseEntity<ResponseDto<String>> updateStatus(
			@RequestBody UpdateStatusPost request
	) {
		Long accountId = authenticationService.getAccountIdFromContext();
		postBuyService.updateStatus(request.getPostId(), accountId, request.getStatus());
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, "Cập nhập trạng thái thành công"));
	}

	@Operation(summary = "update infor post", operationId = "buy.update")
	@PutMapping("")
	public ResponseEntity<ResponseDto<String>> updatePost(@RequestBody UpdatePostBuyRequestDto requestDto) {
		Long accountId = authenticationService.getAccountIdFromContext();
		if (accountId != requestDto.getAuthorId()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn không phải tác giả của bài viết");
		}
		postBuyService.updatePostBuy(requestDto);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, "Cập nhập thành công"));
	}

	@GetMapping("/matching")
	public ResponseEntity<ResponseDto<List<PostSellResponseDto>>> findPostSellMatch(
			@RequestParam("postBuyId") Long postBuyId
	) {
		Long accountId = authenticationService.getAccountIdFromContext();
		List<PostSellResponseDto> entities = postBuyService.findSellMatch(postBuyId, accountId);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, entities));
	}
}
