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
public class PostBuyEntity {
	Long postBuyId;
	Long authorId;

	String title;
	String description;

	String typeProperty;
	Long minAcreage;
	Long maxAcreage;
	Long minPrice;
	Long maxPrice;

	Integer minBathroom;
	Integer minParking;
	Integer minBedroom;
	Integer minFloor;
	List<String> legalDocuments;
	List<String> directionProperties;
	Long minHorizontal;
	Long minVertical;

	String provinceId;
	String nameProvince;
	List<String> nameDistricts;
	List<String> districtIds;

	String postStatus;

	Boolean isAvailable;
	Long createdAt;
	Long updatedAt;
}
