package com.real_estate.post.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostBuyRequestDto {
	String title;
	String description;

	List<String> typeProperty;
	Long minAcreage;
	Long fromPrice;
	Long toPrice;
	Float fromPricePerM2;
	Float toPricePerM2;
	Integer minBathroom;
	Integer minParking;
	Integer minBedroom;
	Integer minFloor;
	List<String> legalDocuments;
	List<String> directionsProperty;

	Long minHorizontal = 0L;
	Long minVertical = 0L;

	String province;
	List<String> district;

}
