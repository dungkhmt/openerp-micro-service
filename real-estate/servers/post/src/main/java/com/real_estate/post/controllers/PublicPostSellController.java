package com.real_estate.post.controllers;

import com.real_estate.post.dtos.response.PageResponseDto;
import com.real_estate.post.dtos.response.PostSellResponseDto;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.models.PostSellEntity;
import com.real_estate.post.services.PostSellService;
import com.real_estate.post.utils.DirectionsStatus;
import com.real_estate.post.utils.TypeProperty;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/post/sell")
public class PublicPostSellController {
    @Autowired
    PostSellService postSellService;

    @GetMapping()
    @Operation(summary = "Get page sell", operationId = "publicSell.getPage")
    public ResponseEntity<PageResponseDto<PostSellResponseDto>> getPageSell(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "province", required = false) String province,
            @RequestParam(value = "district", required = false) String district,
            @RequestParam(value = "fromAcreage", defaultValue = "0") Long fromAcreage,
            @RequestParam(value = "toAcreage", defaultValue = "500") Long toAcreage,
            @RequestParam(value = "fromPrice", required = false, defaultValue = "0") Long fromPrice,
            @RequestParam(value = "toPrice", required = false, defaultValue = "99999999999") Long toPrice,
            @RequestParam(value = "typeProperties", required = false) List<TypeProperty> typeProperties,
            @RequestParam(value = "directions", required = false) List<DirectionsStatus> directions
    ) {
        List<String> typePropertiesString = typeProperties.stream().map((item) -> {
            return item.toString();
        }).collect(Collectors.toList());
        List<String> directionsString = directions.stream().map((item) -> {
            return item.toString();
        }).collect(Collectors.toList());
        Page<PostSellResponseDto> sells = postSellService.getPageSell(page,
                                                                      size,
                                                                      province,
                                                                      district,
                                                                      fromAcreage,
                                                                      toAcreage,
                                                                      fromPrice,
                                                                      toPrice,
                                                                      typePropertiesString,
                                                                      directionsString
        );
        return ResponseEntity.status(HttpStatus.OK).body(new PageResponseDto<PostSellResponseDto>(200, sells));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detail post sell by postsellId", operationId = "sell.getPostById")
    public ResponseEntity<ResponseDto<PostSellEntity>> getPostSellById(
            @PathVariable("id") Long postSellId
    ) {
        PostSellEntity entity = postSellService.getSellById(postSellId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, entity));
    }

    @GetMapping("/my-post/{accountId}")
    @Operation(summary = "Get list post of accountId", operationId = "sell.mypostsell")
    public ResponseEntity<ResponseDto<List<PostSellEntity>>> getPostSellBy(@PathVariable("accountId") Long accountId) {
        List<PostSellEntity> entities = postSellService.getPostByAccountId(accountId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, entities));
    }

}
