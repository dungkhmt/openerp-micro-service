package com.real_estate.post.services;

import com.real_estate.post.daos.interfaces.DashboardDao;
import com.real_estate.post.dtos.response.DashboardTopResponseDto;
import com.real_estate.post.models.DashboardEntity;
import com.real_estate.post.utils.TypeProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DashboardService {
    @Autowired
    @Qualifier("dashboardImpl")
    DashboardDao dashboardDao;

    public Long getLastTimeTrigger() {
        return dashboardDao.getLastTimeTrigger();
    }

    public List<DashboardEntity> saveAll(List<DashboardEntity> entities) {
        return dashboardDao.saveAll(entities);
    }

    public List<DashboardEntity> getBy(Long fromTime, Long toTime, TypeProperty typeProperty, String districtId) {
        List<DashboardEntity> entities = dashboardDao.findBy(fromTime, toTime, typeProperty, districtId);
        if (entities.size() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không có dữ liệu phù hợp");
        }
        return entities;
    }

    public List<DashboardTopResponseDto> getTop(String provinceId, TypeProperty typeProperty, Long startTime) {
        return dashboardDao.find5mediumHighest(provinceId, typeProperty, startTime);
    }
}
