package com.real_estate.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DistrictEntity {
	String districtId;
	String nameDistrict;
	String provinceId;
	String nameProvince;
}
