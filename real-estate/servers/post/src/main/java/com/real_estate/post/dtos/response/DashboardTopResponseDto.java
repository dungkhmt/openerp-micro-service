package com.real_estate.post.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardTopResponseDto {
    String districtId;
    String nameDistrict;
    Double mediumPrice;
}
