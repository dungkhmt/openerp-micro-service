package com.real_estate.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
// test
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostSellEntity {
	Long postId;
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
	String legalDocuments;
	String directionsProperty;
	Long horizontal;
	Long vertical;

	List<Float> position;
	String province;
	String district;
	String address;

	List<String> imageUrls;
	String postStatus;

	Boolean isAvailable;
	Long createdAt;
	Long updatedAt;
}
