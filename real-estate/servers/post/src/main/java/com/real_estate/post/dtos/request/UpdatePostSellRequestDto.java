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
public class UpdatePostSellRequestDto {
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
    LegalDocument legalDocument;
    DirectionsStatus directionsProperty;
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
