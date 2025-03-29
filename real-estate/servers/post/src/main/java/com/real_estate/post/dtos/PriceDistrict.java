package com.real_estate.post.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceDistrict {
    String districtId;
    String nameDistrict;
    Double mediumHouse;
    Double mediumApartment;
    Double mediumLand;
    Long totalPost;
    Long startTime;
}
