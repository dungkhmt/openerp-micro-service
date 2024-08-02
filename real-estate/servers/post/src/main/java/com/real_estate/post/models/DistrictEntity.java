package com.real_estate.post.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DistrictEntity {
	String districtId;
	String nameDistrict;
	String provinceId;
	String nameProvince;
}
