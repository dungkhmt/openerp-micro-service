package com.real_estate.post.services;

import com.real_estate.post.daos.interfaces.DashboardPriceDao;
import com.real_estate.post.dtos.response.DashboardTopResponseDto;
import com.real_estate.post.models.DashboardPriceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DashboardPriceService {
    @Autowired
    @Qualifier("dashboardPriceImpl")
    DashboardPriceDao dashboardPriceDao;

    public Long getLastTimeTrigger() {
        return dashboardPriceDao.getLastTimeTrigger();
    }

    public List<DashboardPriceEntity> saveAll(List<DashboardPriceEntity> entities) {
        return dashboardPriceDao.saveAll(entities);
    }

    public List<DashboardPriceEntity> getBy(Long fromTime, Long toTime, String typeProperty, String districtId) {
        List<DashboardPriceEntity> entities = dashboardPriceDao.findBy(fromTime, toTime, typeProperty, districtId);
        if (entities.size() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không có dữ liệu phù hợp");
        }
        return entities;
    }

    public List<DashboardTopResponseDto> getTop(String provinceId, String typeProperty, Long startTime) {
        return dashboardPriceDao.find5mediumHighest(provinceId, typeProperty, startTime);
    }
}
