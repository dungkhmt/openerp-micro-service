package com.real_estate.post.daos.interfaces;



import com.real_estate.common.models.DistrictEntity;
import com.real_estate.post.dtos.ProvinceEntity;

import java.util.List;

public interface DistrictDao {
	public void saveAll(List<DistrictEntity> entities);

	public List<ProvinceEntity> findDistinctProvince();

	public List<DistrictEntity> findDistrictsBy(String nameProvince);
}
