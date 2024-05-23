package com.real_estate.post.services;

import com.real_estate.post.daos.interfaces.DashboardPriceDao;
import com.real_estate.post.models.DashboardPriceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
}
