package com.real_estate.post.controllers;

import com.real_estate.post.dtos.request.CreatePostBuyRequestDto;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.services.AuthenticationService;
import com.real_estate.post.services.PostBuyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post/buy")
public class PostBuyController {
	@Autowired
	PostBuyService buyService;

	@Autowired
	AuthenticationService authenticationService;

	@PostMapping()
	@Operation(summary = "create a new post buy", operationId = "buy.createPost")
	public ResponseEntity<ResponseDto<String>> createPostBuy(@Valid @RequestBody CreatePostBuyRequestDto requestDto) {
		Long accountId = authenticationService.getAccountIdFromContext();
		buyService.createPostBuy(requestDto, accountId);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, "Đăng tin mua thành công"));
	}

//	@GetMapping()
//	@Operation(summary = "get post buy paging", operationId = "buy.getPagePost")
//	public ResponseEntity<ResponseDto<String>> getPagePostBuy(
//		@RequestParam()
//	)
}
