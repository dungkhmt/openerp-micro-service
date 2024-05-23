package com.real_estate.post.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DashboardPriceEntity {
    Long dashboardPriceId;
    String districtId;
    String nameDistrict;
    String typeProperty;
    Float highestPricePerM2;
    Float lowestPricePerM2;
    Double mediumPricePerM2;
    Long startTime;
    Long endTime;

    public DashboardPriceEntity(String districtId,
                                String nameDistrict,
                                String typeProperty,
                                Float highestPricePerM2,
                                Float lowestPricePerM2,
                                Double mediumPricePerM2,
                                Long startTime,
                                Long endTime
    ) {
        this.districtId = districtId;
        this.nameDistrict = nameDistrict;
        this.typeProperty = typeProperty;
        this.highestPricePerM2 = highestPricePerM2;
        this.lowestPricePerM2 = lowestPricePerM2;
        this.mediumPricePerM2 = mediumPricePerM2;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}
