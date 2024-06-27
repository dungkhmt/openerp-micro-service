package com.real_estate.post.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceDistrictQuery {
    String districtId;
    String nameDistrict;
    Long startTime;
}
