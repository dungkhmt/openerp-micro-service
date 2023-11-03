package openerp.openerpresourceserver.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import openerp.openerpresourceserver.common.CommonUtil;
import openerp.openerpresourceserver.model.dto.request.FilterScheduleDto;
import openerp.openerpresourceserver.model.entity.*;
import openerp.openerpresourceserver.repo.*;
import openerp.openerpresourceserver.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    public static final Integer MAX_CREW_PER_DAY = 12;

    @Autowired
    private ScheduleRepo scheduleRepo;

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<Schedule> searchSchedule(FilterScheduleDto searchDto) {
        StringBuilder jpql = this.getStringBuilder(searchDto);

        Query query = entityManager.createQuery(jpql.toString());
        query.setParameter("classRoom", searchDto.getClassRoom());

        if (searchDto.getClassCode() != null) {
            query.setParameter("classCode", searchDto.getClassCode());
        }
        if (searchDto.getClassType() != null) {
            query.setParameter("classType", searchDto.getClassType());
        }
        if (searchDto.getInstitute() != null) {
            query.setParameter("institute", searchDto.getInstitute());
        }
        if (searchDto.getManagementCode() != null) {
            query.setParameter("managementCode", searchDto.getManagementCode());
        }
        if (searchDto.getModuleCode() != null) {
            query.setParameter("moduleCode", searchDto.getModuleCode());
        }
        if (searchDto.getOpenBatch() != null) {
            query.setParameter("openBatch", searchDto.getOpenBatch());
        }
        if (searchDto.getSemester() != null) {
            query.setParameter("semester", searchDto.getSemester());
        }
        if (searchDto.getState() != null) {
            query.setParameter("state", searchDto.getState());
        }
        if (searchDto.getStudyTime() != null) {
            query.setParameter("studyTime", searchDto.getStudyTime());
        }
        if (searchDto.getStudyWeek() != null) {
            query.setParameter("studyWeek", searchDto.getStudyWeek());
        }
        if (searchDto.getWeekDay() != null) {
            query.setParameter("weekDay", searchDto.getWeekDay());
        }

        // Execute the query and return the result list
        return query.getResultList();
    }

    private StringBuilder getStringBuilder(FilterScheduleDto searchDto) {
        StringBuilder jpql = new StringBuilder("SELECT s FROM Schedule s WHERE ");
        jpql.append(" s.classRoom = :classRoom");

        if (searchDto.getClassCode() != null) {
            jpql.append(" AND s.classCode = :classCode");
        }
        if (searchDto.getClassType() != null) {
            jpql.append(" AND s.classType = :classType");
        }
        if (searchDto.getInstitute() != null) {
            jpql.append(" AND s.institute = :institute");
        }
        if (searchDto.getManagementCode() != null) {
            jpql.append(" AND s.managementCode = :managementCode");
        }
        if (searchDto.getModuleCode() != null) {
            jpql.append(" AND s.moduleCode = :moduleCode");
        }
        if (searchDto.getOpenBatch() != null) {
            jpql.append(" AND s.openBatch = :openBatch");
        }
        if (searchDto.getSemester() != null) {
            jpql.append(" AND s.semester = :semester");
        }
        if (searchDto.getState() != null) {
            jpql.append(" AND s.state = :state");
        }
        if (searchDto.getStudyTime() != null) {
            jpql.append(" AND s.studyTime = :studyTime");
        }
        if (searchDto.getStudyWeek() != null) {
            jpql.append(" AND s.studyWeek = :studyWeek");
        }
        if (searchDto.getWeekDay() != null) {
            jpql.append(" AND s.weekDay = :weekDay");
        }
        return jpql;
    }

    @Override
    public String calculateTimePerformance(FilterScheduleDto requestDto) {
        List<Schedule> scheduleList = this.searchSchedule(requestDto);
        int totalTimeUsing = 0;
        for (Schedule schedule : scheduleList) {
            String start = schedule.getStart();
            String finish = schedule.getFinish();
            int countCrew = CommonUtil.calculateTimeCrew(start, finish);
            totalTimeUsing += countCrew;
        }
        double result = (double) totalTimeUsing * 100 / MAX_CREW_PER_DAY;
        double roundedResult = Math.round(result * 100.0) / 100.0;

        return roundedResult + " %";
    }

//    public String calculateSeatPerformance(List<Schedule> scheduleList) {
//
//    }
}
