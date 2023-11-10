package openerp.openerpresourceserver.common;

import jakarta.persistence.Query;
import openerp.openerpresourceserver.model.dto.request.FilterScheduleDto;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CommonUtil {

    public static final Integer TIME_FOR_A_CREW = 50;

    public static final Integer MINUTE_PER_HOUR = 60;

    public static Integer roundUpDivision(Integer dividend, Integer divisor) {
        BigDecimal result = new BigDecimal(dividend).divide(new BigDecimal(divisor), 0, RoundingMode.CEILING);
        return result.intValue();
    }

    public static Integer calculateTimeCrew(String start, String finish) {
        int timeStudy = 0;
        if (start.length() < 2 && finish.length() < 2) {
            Integer begin = Integer.parseInt(start);
            Integer end = Integer.parseInt(finish);
            return (end - begin) + 1;
        }
        Integer hourStart = Integer.parseInt(start.substring(0, 2));
        Integer minuteStart = Integer.parseInt(start.substring(2, 4));
        Integer hourFinish = Integer.parseInt(finish.substring(0, 2));
        Integer minuteFinish = Integer.parseInt(finish.substring(2, 4));

        int tmpMinute = minuteFinish - minuteStart;
        int tmpHour = hourFinish - hourStart;
        if (tmpMinute < 0) {
            timeStudy = ( tmpHour - 1) * MINUTE_PER_HOUR + (tmpMinute + MINUTE_PER_HOUR);
        } else {
            timeStudy = tmpHour * MINUTE_PER_HOUR + tmpMinute;
        }
        return roundUpDivision(timeStudy, TIME_FOR_A_CREW);
    }

    public static StringBuilder appendAttributes(StringBuilder jpql, FilterScheduleDto searchDto ){
        if (searchDto.getClassRoom() != null) {
            jpql.append(" AND s.classRoom = :classRoom");
        }
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

    public static Query buildQuery(Query query, FilterScheduleDto searchDto) {

        if (searchDto.getClassRoom() != null) {
            query.setParameter("classRoom", searchDto.getClassRoom());
        }
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
        return query;
    }
}
