package com.real_estate.post.controllers;

import com.real_estate.post.dtos.response.PageResponseDto;
import com.real_estate.post.dtos.response.PostBuyResponseDto;
import com.real_estate.post.services.PostBuyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
//            @RequestParam(value = "minAcreage", defaultValue = "0") Long minAcreage,
//            @RequestParam(value = "fromPrice", required = false) Long fromPrice,
//            @RequestParam(value = "toPrice", required = false) Long toPrice,
//            @RequestParam(value = "sortPrice", required = false) String sortPrice,
//            @RequestParam(value = "typeProperties", required = false) List<TypeProperty> typeProperties,
//            @RequestParam(value = "legalDocuments", required = false) List<LegalDocuments> legalDocuments,
//            @RequestParam(value = "directions", required = false) List<DirectionsStatus> directions,
//            @RequestParam(value = "minFloor", defaultValue = "0", required = false) Long minFloor,
//            @RequestParam(value = "minBathroom", defaultValue = "0", required = false) Long minBathroom,
//            @RequestParam(value = "minBedroom", defaultValue = "0", required = false) Long minBedroom,
//            @RequestParam(value = "minParking", defaultValue = "0", required = false) Long minParking
    ) {
        Page<PostBuyResponseDto> result = postBuyService.getPageBuy(page, size, province, district);
        return ResponseEntity.status(HttpStatus.OK).body(new PageResponseDto<PostBuyResponseDto>(200, result));
    }

}
