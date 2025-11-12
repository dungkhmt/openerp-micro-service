package com.real_estate.post.models.postgresql;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(
	name = "district"
)
public class DistrictPostgresEntity {
	@Id
	@Column(name = "district_id")
	String districtId;

	@Column(name = "name_district")
	String nameDistrict;

	@Column(name = "province_id")
	String provinceId;

	@Column(name = "name_province")
	String nameProvince;
}
