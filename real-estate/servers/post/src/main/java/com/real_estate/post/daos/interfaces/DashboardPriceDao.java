package com.real_estate.post.daos.interfaces;

import com.real_estate.post.dtos.PriceDistrict;
import com.real_estate.post.dtos.PriceDistrictQuery;
import com.real_estate.post.dtos.response.DashboardTopResponseDto;
import com.real_estate.post.models.DashboardPriceEntity;

import java.util.List;

public interface DashboardPriceDao {
    public DashboardPriceEntity save(DashboardPriceEntity entity);

    public List<DashboardPriceEntity> saveAll(List<DashboardPriceEntity> entities);

    public Long getLastTimeTrigger();

    public List<DashboardPriceEntity> findBy(Long fromTime, Long toTime, String typeProperty, String districtId);

    public List<PriceDistrict> findPriceDistricts(List<PriceDistrictQuery> queries);

    public List<DashboardTopResponseDto> find5mediumHighest(String provinceId, String typeProperty, Long startTime);
}
