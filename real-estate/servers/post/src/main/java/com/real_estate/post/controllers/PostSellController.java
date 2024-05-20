package com.real_estate.post.controllers;

import com.real_estate.post.dtos.request.CreatePostSellRequestDto;
import com.real_estate.post.dtos.request.UpdatePostSellRequestDto;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.services.AuthenticationService;
import com.real_estate.post.services.PostSellService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
	@PostMapping("/update")
	public ResponseEntity<ResponseDto<String>> updatePost(@RequestBody UpdatePostSellRequestDto requestDto) {
		Long accountId = authenticationService.getAccountIdFromContext();
		if (accountId != requestDto.getAuthorId()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bạn không phải tác giả của bài viết");
		}
		postSellService.updatePostSell(requestDto);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, "Cập nhập thành công"));
	}

}
