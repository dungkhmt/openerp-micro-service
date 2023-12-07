package openerp.openerpresourceserver.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import openerp.openerpresourceserver.common.CommonUtil;
import openerp.openerpresourceserver.model.dto.request.FilterScheduleDto;
import openerp.openerpresourceserver.model.entity.TimePerformance;
import openerp.openerpresourceserver.repo.TimePerformanceRepo;
import openerp.openerpresourceserver.service.TimePerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TimePerformanceServiceImpl implements TimePerformanceService {

    @Autowired
    private TimePerformanceRepo timePerformanceRepo;

    @Autowired
    private EntityManager entityManager;

    //--------------------Search time performance------------------
    @Override
    public List<TimePerformance> getTimePerformance(FilterScheduleDto requestDto) {
        StringBuilder jpql = this.getStringBuilderForTimePerformance(requestDto);

        Query query = entityManager.createQuery(jpql.toString());

        // Execute the query and return the result list
        return CommonUtil.buildQuery(query, requestDto).getResultList();
    }

    private StringBuilder getStringBuilderForTimePerformance(FilterScheduleDto searchDto) {
        StringBuilder jpql = new StringBuilder("SELECT s FROM TimePerformance s WHERE 1 = 1");

        return CommonUtil.appendAttributes(jpql, searchDto);
    }

    @Override
    public List<TimePerformance> getAll() {
        return timePerformanceRepo.findAll();
    }

    @Override
    @Transactional
    public void deleteByIdList(List<Long> idList) {
        try {
            idList.forEach(el -> {
                timePerformanceRepo.deleteById(el);
            });
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }
}
