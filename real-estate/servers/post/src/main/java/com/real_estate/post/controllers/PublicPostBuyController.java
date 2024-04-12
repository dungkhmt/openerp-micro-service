package com.real_estate.post.controllers;

import com.real_estate.common.dtos.response.ResponseDto;
import com.real_estate.common.models.PostBuyEntity;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/post/buy")
public class PublicPostBuyController {
    @GetMapping("")
    @Operation(summary = "Get page post buy", operationId = "publicBuy.getPost")
    public ResponseEntity<ResponseDto<PostBuyEntity>> getPagePostBuy(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ) {
        return null;
    }

}
