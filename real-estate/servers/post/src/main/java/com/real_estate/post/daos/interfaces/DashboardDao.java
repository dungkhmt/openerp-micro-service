package com.real_estate.post.daos.interfaces;

import com.real_estate.post.dtos.PriceDistrict;
import com.real_estate.post.dtos.PriceDistrictQuery;
import com.real_estate.post.dtos.response.DashboardTopResponseDto;
import com.real_estate.post.models.DashboardEntity;
import com.real_estate.post.utils.TypeProperty;

import java.util.List;

public interface DashboardDao {
    public DashboardEntity save(DashboardEntity entity);

    public List<DashboardEntity> saveAll(List<DashboardEntity> entities);

    public Long getLastTimeTrigger();

    public List<DashboardEntity> findBy(Long fromTime, Long toTime, TypeProperty typeProperty, String districtId);

    public List<PriceDistrict> findPriceDistricts(List<PriceDistrictQuery> queries);

    public List<DashboardTopResponseDto> find5mediumHighest(String provinceId, TypeProperty typeProperty, Long startTime);
}
