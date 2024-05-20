package com.real_estate.post.dtos.response;

import com.real_estate.post.models.AccountEntity;
import com.real_estate.post.models.PostSellEntity;
import lombok.*;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSellResponseDto {
    Long postSellId;
    Long authorId;
    String nameAuthor;
    String avatarAuthor;
    String phoneAuthor;
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

    public PostSellResponseDto(PostSellEntity p, AccountEntity a) {
        this.postSellId = p.getPostSellId();
        this.authorId = p.getAuthorId();
        this.nameAuthor = a.getName();
        this.avatarAuthor = a.getAvatar();
        this.phoneAuthor = a.getPhone();
        this.title = p.getTitle();
        this.description = p.getDescription();
        this.typeProperty = p.getTypeProperty();
        this.price = p.getPrice();
        this.pricePerM2 = p.getPricePerM2();
        this.acreage = p.getAcreage();
        this.bathroom = p.getBathroom();
        this.parking = p.getParking();
        this.bedroom = p.getBedroom();
        this.floor = p.getFloor();
        this.legalDocuments = p.getLegalDocuments();
        this.directionsProperty = p.getDirectionsProperty();
        this.horizontal = p.getHorizontal();
        this.vertical = p.getVertical();
        this.position = p.getPosition();
        this.province = p.getProvince();
        this.district = p.getDistrict();
        this.address = p.getAddress();
        this.imageUrls = p.getImageUrls();
        this.postStatus = p.getPostStatus();
        this.isAvailable = p.getIsAvailable();
        this.createdAt = p.getCreatedAt();
        this.updatedAt = p.getUpdatedAt();
    }
}
