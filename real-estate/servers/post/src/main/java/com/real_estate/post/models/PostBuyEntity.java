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
public class PostBuyEntity {
	Long postBuyId;
	Long authorId;

	String title;
	String description;

	TypeProperty typeProperty;
	Long minAcreage;
	Long maxAcreage;
	Long minPrice;
	Long maxPrice;

	Integer minBathroom;
	Integer minParking;
	Integer minBedroom;
	Integer minFloor;
	List<TypeLegalDocument> legalDocuments;
	List<TypeDirection> directionProperties;
	Float minHorizontal;
	Float minVertical;

	String provinceId;
	String nameProvince;
	List<String> nameDistricts;
	List<String> districtIds;

	PostStatus postStatus;

	Boolean isAvailable; // sau nay de admin quan ly
	Long createdAt;
	Long updatedAt;
}
