package com.real_estate.post.dtos.response;

import com.real_estate.post.models.AccountEntity;
import com.real_estate.post.models.PostBuyEntity;
import com.real_estate.post.models.postgresql.AccountPostgresEntity;
import com.real_estate.post.models.postgresql.PostBuyPostgresEntity;
import com.real_estate.post.models.postgresql.SavePostPostgresEntity;
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
    String emailAuthor;

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

    Long saveId = 0L;
    public PostBuyResponseDto(PostBuyEntity p, AccountEntity a) {
        this.postBuyId = p.getPostBuyId();
        this.authorId = p.getAuthorId();
        this.nameAuthor = a.getName();
        this.avatarAuthor = a.getAvatar();
        this.phoneAuthor = a.getPhone();
        this.emailAuthor = a.getEmail();
        this.title = p.getTitle();
        this.description = p.getDescription();
        this.typeProperty = p.getTypeProperty();
        this.minAcreage = p.getMinAcreage();
        this.maxAcreage = p.getMaxAcreage();
        this.minPrice = p.getMinPrice();
        this.maxPrice = p.getMaxPrice();

        this.minBathroom = p.getMinBathroom();
        this.minParking = p.getMinParking();
        this.minBedroom = p.getMinBedroom();
        this.minFloor = p.getMinFloor();
        this.legalDocuments = p.getLegalDocuments();
        this.directionProperties = p.getDirectionProperties();
        this.minHorizontal = p.getMinHorizontal();
        this.minVertical = p.getMinVertical();

        this.provinceId = p.getProvinceId();
        this.nameProvince = p.getNameProvince();
        this.districtIds = p.getDistrictIds();
        this.nameDistricts = p.getNameDistricts();

        this.postStatus = p.getPostStatus();
        this.isAvailable = p.getIsAvailable();
        this.createdAt = p.getCreatedAt();
        this.updatedAt = p.getUpdatedAt();
    }
    public PostBuyResponseDto(PostBuyPostgresEntity p, AccountPostgresEntity a, SavePostPostgresEntity s) {
        this.postBuyId = p.getPostBuyId();
        this.authorId = p.getAuthorId();
        this.nameAuthor = a.getName();
        this.avatarAuthor = a.getAvatar();
        this.phoneAuthor = a.getPhone();
        this.emailAuthor = a.getEmail();
        this.title = p.getTitle();
        this.description = p.getDescription();
        this.typeProperty = p.getTypeProperty();
        this.minAcreage = p.getMinAcreage();
        this.maxAcreage = p.getMaxAcreage();
        this.minPrice = p.getMinPrice();
        this.maxPrice = p.getMaxPrice();

        this.minBathroom = p.getMinBathroom();
        this.minParking = p.getMinParking();
        this.minBedroom = p.getMinBedroom();
        this.minFloor = p.getMinFloor();
        this.legalDocuments = p.getLegalDocuments();
        this.directionProperties = p.getDirectionProperties();
        this.minHorizontal = p.getMinHorizontal();
        this.minVertical = p.getMinVertical();

        this.provinceId = p.getProvinceId();
        this.nameProvince = p.getNameProvince();
        this.districtIds = p.getDistrictIds();
        this.nameDistricts = p.getNameDistricts();

        this.postStatus = p.getPostStatus();
        this.isAvailable = p.getIsAvailable();
        this.createdAt = p.getCreatedAt();
        this.updatedAt = p.getUpdatedAt();

        this.saveId = s.getSaveId();
    }

}
