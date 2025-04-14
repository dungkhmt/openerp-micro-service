package com.real_estate.post.controllers;

import com.real_estate.post.dtos.request.CreatePostSellRequestDto;
import com.real_estate.post.dtos.request.UpdatePostSellRequestDto;
import com.real_estate.post.dtos.request.UpdateStatusPost;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.models.PostSellEntity;
import com.real_estate.post.services.AuthenticationService;
import com.real_estate.post.services.PostSellService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController()
@RequestMapping(path = "/post/sell")
public class PostSellController {
	@Autowired
	PostSellService postSellService;

	@Autowired
	AuthenticationService authenticationService;

	@Operation(summary = "create a post sell", operationId = "sell.createPost")
	@PostMapping("")
	public ResponseEntity<ResponseDto<String>> createPost(
		@RequestBody CreatePostSellRequestDto requestDto
	) {
		Long accountId = authenticationService.getAccountIdFromContext();
		postSellService.createPostSell(requestDto, accountId);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, "Đăng Bán Thành Công"));
	}

	@Operation(summary = "update infor post", operationId = "sell.update")
	@PutMapping("")
	public ResponseEntity<ResponseDto<String>> updatePost(@RequestBody UpdatePostSellRequestDto requestDto) {
		Long accountId = authenticationService.getAccountIdFromContext();
		if (accountId != requestDto.getAuthorId()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn không phải tác giả của bài viết");
		}
		postSellService.updatePostSell(requestDto);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, "Cập nhập thành công"));
	}

	@PutMapping("/updateStatus")
	@Operation(operationId = "postsell.updateStatus", summary = "update status of post")
	public ResponseEntity<ResponseDto<String>> updateStatus(
			@RequestBody UpdateStatusPost request
	) {
		Long accountId = authenticationService.getAccountIdFromContext();
		postSellService.updateStatus(request.getPostId(), accountId, request.getStatus());
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, "Cập nhập trạng thái thành công"));
	}

}
