package com.real_estate.post.controllers;

import com.real_estate.post.dtos.response.PageResponseDto;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.models.PostSellEntity;
import com.real_estate.post.services.PostSellService;
import com.real_estate.post.utils.DirectionsStatus;
import com.real_estate.post.utils.LegalDocuments;
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
    public ResponseEntity<PageResponseDto<PostSellEntity>> getPageSell(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "province", required = false) String province,
            @RequestParam(value = "district", required = false) String district,
            @RequestParam(value = "minAcreage", defaultValue = "0") Long minAcreage,
            @RequestParam(value = "fromPrice", required = false) Long fromPrice,
            @RequestParam(value = "toPrice", required = false) Long toPrice,
            @RequestParam(value = "sortPrice", required = false) String sortPrice,
            @RequestParam(value = "typeProperties", required = false) List<TypeProperty> typeProperties,
            @RequestParam(value = "legalDocuments", required = false) List<LegalDocuments> legalDocuments,
            @RequestParam(value = "directions", required = false) List<DirectionsStatus> directions,
            @RequestParam(value = "minFloor", defaultValue = "0", required = false) Long minFloor,
            @RequestParam(value = "minBathroom", defaultValue = "0", required = false) Long minBathroom,
            @RequestParam(value = "minBedroom", defaultValue = "0", required = false) Long minBedroom,
            @RequestParam(value = "minParking", defaultValue = "0", required = false) Long minParking
    ) {
        List<String> typePropertiesString = typeProperties.stream().map((item) -> {
            return item.toString();
        }).collect(Collectors.toList());

        List<String> legalDocumentsString = legalDocuments.stream().map((item) -> {
            return item.toString();
        }).collect(Collectors.toList());

        List<String> directionsString = directions.stream().map((item) -> {
            return item.toString();
        }).collect(Collectors.toList());
        Page<PostSellEntity> sells = postSellService.getPageSell(page,
                                                                 size,
                                                                 province,
                                                                 district,
                                                                 minAcreage,
                                                                 fromPrice,
                                                                 toPrice,
                                                                 sortPrice,
                                                                 typePropertiesString,
                                                                 legalDocumentsString,
                                                                 directionsString,
                                                                 minFloor,
                                                                 minBathroom,
                                                                 minBedroom,
                                                                 minParking
        );
        return ResponseEntity.status(HttpStatus.OK).body(new PageResponseDto<PostSellEntity>(200, sells));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detail post sell by postsellId", operationId = "sell.getPostById")
    public ResponseEntity<ResponseDto<PostSellEntity>> getPostSellById(
            @PathVariable("id") Long postSellId
    ) {
        PostSellEntity entity = postSellService.getSellById(postSellId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, entity));
    }

}
