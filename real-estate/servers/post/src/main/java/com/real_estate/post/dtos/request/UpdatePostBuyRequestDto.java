package com.real_estate.post.dtos.request;

import com.real_estate.post.utils.DirectionsStatus;
import com.real_estate.post.utils.LegalDocument;
import com.real_estate.post.utils.TypeProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePostBuyRequestDto {
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
    List<LegalDocument> legalDocuments;
    List<DirectionsStatus> directionProperties;
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
