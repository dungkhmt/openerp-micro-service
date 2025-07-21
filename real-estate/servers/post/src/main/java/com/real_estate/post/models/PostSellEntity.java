package com.real_estate.post.models;

import com.real_estate.post.utils.PostStatus;
import com.real_estate.post.utils.TypeDirection;
import com.real_estate.post.utils.TypeLegalDocument;
import com.real_estate.post.utils.TypeProperty;
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

	TypeProperty typeProperty;
	Long price;
	Float pricePerM2;
	Long acreage;
	Integer bathroom;
	Integer parking;
	Integer bedroom;
	Integer floor;
	TypeLegalDocument legalDocument;
	TypeDirection directionProperty;
	Float horizontal;
	Float vertical;

	List<Float> position;

	String provinceId;
	String nameProvince;
	String districtId;
	String nameDistrict;

	String address;

	List<String> imageUrls;
	PostStatus postStatus;

	Boolean isAvailable; // sau nay de admin quan ly
	Long createdAt;
	Long updatedAt;
}
