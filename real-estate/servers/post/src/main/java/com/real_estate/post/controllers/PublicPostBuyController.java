package com.real_estate.post.controllers;

import com.real_estate.post.dtos.response.PageResponseDto;
import com.real_estate.post.dtos.response.PostBuyResponseDto;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.services.PostBuyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/post/buy")
public class PublicPostBuyController {
    private final PostBuyService postBuyService;

    public PublicPostBuyController(PostBuyService postBuyService) {
        this.postBuyService = postBuyService;
    }

    @GetMapping("")
    @Operation(summary = "Get page post buy", operationId = "publicBuy.getPost")
    public ResponseEntity<PageResponseDto<PostBuyResponseDto>> getPagePostBuy(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "province", required = false) String province,
            @RequestParam(value = "district", required = false) String district
    ) {
        Page<PostBuyResponseDto> result = postBuyService.getPageBuy(page, size, province, district);
        return ResponseEntity.status(HttpStatus.OK).body(new PageResponseDto<PostBuyResponseDto>(200, result));
    }

    @GetMapping("/my-post/{accountId}")
    @Operation(summary = "Get list post of accountId", operationId = "buy.mypostbuy")
    public ResponseEntity<ResponseDto<List<PostBuyResponseDto>>> getPostBuyBy(@PathVariable("accountId") Long accountId) {
        System.out.println("account" + accountId);
        List<PostBuyResponseDto> entities = postBuyService.getPostByAccountId(accountId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, entities));
    }

}
