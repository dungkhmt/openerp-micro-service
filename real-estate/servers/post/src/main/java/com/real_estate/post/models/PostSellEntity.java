package com.real_estate.post.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostSellEntity {
	Long postSellId;
	Long authorId;

	String title;
	String description;

	String typeProperty;
	Long price;
	Float pricePerM2;
	Long acreage;
	Integer bathroom;
	Integer parking;
	Integer bedroom;
	Integer floor;
	String legalDocument;
	String directionsProperty;
	Long horizontal;
	Long vertical;

	List<Float> position;

	String provinceId;
	String nameProvince;
	String districtId;
	String nameDistrict;

	String address;

	List<String> imageUrls;
	String postStatus;

	Boolean isAvailable;
	Long createdAt;
	Long updatedAt;
}
