package com.real_estate.post.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostBuyEntity {
	Long postBuyId;
	Long authorId;

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
	Long minHorizontal;
	Long minVertical;

	String province;
	List<String> district;
	String postStatus;

	Boolean isAvailable;
	Long createdAt;
	Long updatedAt;
}
