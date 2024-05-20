package com.real_estate.post.dtos.response;

import com.real_estate.post.models.AccountEntity;
import com.real_estate.post.models.PostBuyEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostBuyResponseDto {
    Long postBuyId;
    Long authorId;
    String nameAuthor;
    String avatarAuthor;
    String phoneAuthor;

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

    public PostBuyResponseDto(PostBuyEntity p, AccountEntity a) {
        this.postBuyId = p.getPostBuyId();
        this.authorId = p.getAuthorId();
        this.nameAuthor = a.getName();
        this.avatarAuthor = a.getAvatar();
        this.phoneAuthor = a.getPhone();
        this.title = p.getTitle();
        this.description = p.getDescription();
        this.typeProperty = p.getTypeProperty();
        this.minAcreage = p.getMinAcreage();
        this.fromPrice = p.getFromPrice();
        this.toPrice = p.getToPrice();
        this.fromPricePerM2 = p.getFromPricePerM2();
        this.toPricePerM2 = p.getToPricePerM2();
        this.minBathroom = p.getMinBathroom();
        this.minParking = p.getMinParking();
        this.minBedroom = p.getMinBedroom();
        this.minFloor = p.getMinFloor();
        this.legalDocuments = p.getLegalDocuments();
        this.directionsProperty = p.getDirectionsProperty();
        this.minHorizontal = p.getMinHorizontal();
        this.minVertical = p.getMinVertical();
        this.province = p.getProvince();
        this.district = p.getDistrict();
        this.postStatus = p.getPostStatus();
        this.isAvailable = p.getIsAvailable();
        this.createdAt = p.getCreatedAt();
        this.updatedAt = p.getUpdatedAt();
    }
}
