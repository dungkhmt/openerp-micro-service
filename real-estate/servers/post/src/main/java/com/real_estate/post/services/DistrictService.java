package com.real_estate.post.services;

import com.real_estate.post.daos.interfaces.DistrictDao;
import com.real_estate.post.dtos.Location;
import com.real_estate.post.dtos.ProvinceEntity;
import com.real_estate.post.models.DistrictEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DistrictService {
	@Autowired
	@Qualifier("districtImpl")
	DistrictDao districtDao;

	public void createDistricts(List<Location> locations) {
		List<DistrictEntity> entities = locations.stream().map(location -> {
			DistrictEntity entity = new DistrictEntity();
			entity.setDistrictId(location.getCode());
			entity.setNameDistrict(getDistrict(location.getPath()));
			entity.setProvinceId(location.getParentCode());
			entity.setNameProvince(getProvince(location.getPath()));
			return entity;
		}).collect(Collectors.toList());
		districtDao.saveAll(entities);
	}

	private String getDistrict(String path) {
		String[] parts = path.split(", ");
		return parts[0];
	}

	private String getProvince(String path) {
		String[] parts = path.split(", ");
		return parts[1];
	}

	public List<ProvinceEntity> getProvinces() {
		return districtDao.findDistinctProvince();
	}

	public List<DistrictEntity> getDistricts(String nameProvince) {
		return districtDao.findDistrictsBy(nameProvince);
	}
}
