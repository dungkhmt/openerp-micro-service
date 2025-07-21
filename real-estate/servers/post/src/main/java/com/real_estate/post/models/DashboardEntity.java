package com.real_estate.post.models;

import com.real_estate.post.utils.TypeProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DashboardEntity {
    Long dashboardId;
    String districtId;
    String nameDistrict;
    TypeProperty typeProperty;
    Float highestPricePerM2;
    Float lowestPricePerM2;
    Double mediumPricePerM2;
    Long totalPost;
    Long startTime;
    Long endTime;

    public DashboardEntity(String districtId,
                           String nameDistrict,
                           TypeProperty typeProperty,
                           Float highestPricePerM2,
                           Float lowestPricePerM2,
                           Double mediumPricePerM2,
                           Long totalPost,
                           Long startTime,
                           Long endTime
    ) {
        this.districtId = districtId;
        this.nameDistrict = nameDistrict;
        this.typeProperty = typeProperty;
        this.highestPricePerM2 = highestPricePerM2;
        this.lowestPricePerM2 = lowestPricePerM2;
        this.mediumPricePerM2 = mediumPricePerM2;
        this.totalPost = totalPost;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}
