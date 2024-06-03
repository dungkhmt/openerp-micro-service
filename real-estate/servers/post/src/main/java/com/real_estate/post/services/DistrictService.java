package com.real_estate.post.services;

import com.real_estate.post.daos.interfaces.DashboardPriceDao;
import com.real_estate.post.daos.interfaces.DistrictDao;
import com.real_estate.post.dtos.Location;
import com.real_estate.post.dtos.PriceDistrict;
import com.real_estate.post.dtos.PriceDistrictQuery;
import com.real_estate.post.dtos.ProvinceEntity;
import com.real_estate.post.models.DistrictEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DistrictService {
	@Autowired
	@Qualifier("districtImpl")
	DistrictDao districtDao;

	@Autowired
	@Qualifier("dashboardPriceImpl")
	DashboardPriceDao dashboardPriceDao;

	@Value("${dashboard.duration}")
	Long DURATION;

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

	public List<DistrictEntity> getDistricts(String provinceId) {
		return districtDao.findDistrictsBy(provinceId);
	}

	public Map<String, List<PriceDistrict>> getPriceDistricts(String provinceId, Long fromTime, Long toTime) {
		Long now = System.currentTimeMillis();
		toTime = toTime == 0 ? now : toTime;
		List<DistrictEntity> districts = districtDao.findDistrictsBy(provinceId);
		Long startTime = fromTime % DURATION == 0 ? fromTime : (fromTime - fromTime % DURATION + DURATION);

		List<PriceDistrictQuery> queries = new ArrayList<>();
		while (startTime < toTime - DURATION) {
			Long finalStartTime = startTime;
			queries.addAll(
					districts.stream()
							.map(item -> new PriceDistrictQuery(item.getDistrictId(),
																item.getNameDistrict(),
																finalStartTime
							))
							.collect(Collectors.toList())
			);
			startTime += DURATION;
		}
		List<PriceDistrict> priceDistricts = dashboardPriceDao.findPriceDistricts(queries);
		Map<String, List<PriceDistrict>> priceDistrictMap = new HashMap<>();
		for (PriceDistrict pd : priceDistricts) {
			priceDistrictMap.computeIfAbsent(pd.getDistrictId(), k -> new ArrayList<>()).add(pd);
		}
		return priceDistrictMap;
	}
}
