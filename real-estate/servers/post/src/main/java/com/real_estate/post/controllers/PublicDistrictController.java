package com.real_estate.post.controllers;

import com.real_estate.post.dtos.PriceDistrict;
import com.real_estate.post.dtos.ProvinceEntity;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.models.DistrictEntity;
import com.real_estate.post.services.DistrictService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public/address")
public class PublicDistrictController {
	@Autowired
	DistrictService districtService;

	@GetMapping("/provinces")
	@Operation(operationId = "district.getProvinces", summary = "Get list province of vn")
	public ResponseEntity<ResponseDto<List<ProvinceEntity>>> getProvinces() {
		List<ProvinceEntity> entities = districtService.getProvinces();
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, entities));
	}

	@GetMapping("/district")
	@Operation(operationId = "district.getDistrict", summary = "Get district of province")
	public ResponseEntity<ResponseDto<List<DistrictEntity>>> getDistricts(
		@RequestParam("provinceId") String provinceId
	) {
		List<DistrictEntity> entities = districtService.getDistricts(provinceId);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, entities));
	}

	@GetMapping("/price-district")
	@Operation(operationId = "district.getDistrict", summary = "Get district of province")
	public ResponseEntity<ResponseDto<Map<String, List<PriceDistrict>>>> getPriceOfAllDistrict(
			@RequestParam("provinceId") String provinceId,
			@RequestParam("fromTime") Long fromTime,
			@RequestParam(value = "toTime" ,required = false, defaultValue = "0") Long toTime
	) {
		Map<String, List<PriceDistrict>> result = districtService.getPriceDistricts(provinceId, fromTime, toTime);
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, result));
	}
}
