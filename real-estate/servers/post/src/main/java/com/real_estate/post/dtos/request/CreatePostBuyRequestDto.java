package com.real_estate.post.dtos.request;

import com.real_estate.post.utils.DirectionsStatus;
import com.real_estate.post.utils.LegalDocument;
import com.real_estate.post.utils.TypeProperty;
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

	TypeProperty typeProperty;
	Long minAcreage;
	Long maxAcreage = 0L;
	Long minPrice;
	Long maxPrice = 0L;

	Integer minBathroom = 0;
	Integer minParking = 0;
	Integer minBedroom = 0;
	Integer minFloor = 0;
	List<LegalDocument> legalDocuments;
	List<DirectionsStatus> directionProperties;
	Long minHorizontal = 0L;
	Long minVertical = 0L;

	String provinceId;
	String nameProvince;
	List<String> nameDistricts;
	List<String> districtIds;
}
