package com.real_estate.post.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostSellRequestDto {
	List<Float> position;
	String province;
	String district;
	String address;

	List<String> imageUrls;

	String title;
	String description;
	Long acreage;
	Long price;
	Float pricePerM2;

	String typeProperty;
	String legalDocuments;
	String directionsProperty;
	Integer floor;
	Integer bathroom;
	Integer parking;
	Integer bedroom;
	Long horizontal;
	Long vertical;
}
