package com.real_estate.post.controllers;


import com.real_estate.common.dtos.response.ResponseDto;
import com.real_estate.post.dtos.request.CreatePostSellRequestDto;
import com.real_estate.post.services.PostSellService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(path = "/public/post/sell")
public class PostSellController {
	@Autowired
	PostSellService postSellService;

	@Operation(summary = "create a post sell", operationId = "sell.createPost")
	@PostMapping("")
	public ResponseEntity<ResponseDto<String>> createPost(
		@RequestBody CreatePostSellRequestDto requestDto
	) {
		System.out.println("goi api");
		postSellService.createPostSell(requestDto);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, "ok"));
	}

}
