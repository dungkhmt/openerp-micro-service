package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.dto.request.FilterScheduleDto;
import openerp.openerpresourceserver.model.entity.*;

import java.util.List;

public interface ScheduleService {
    List<Semester> getSemester();

    void updateSemester();

    List<Institute> getInstitute();

    void updateInstitute();

    List<ClassCode> getClassCode();

    void updateClassCode();

    List<Classroom> getClassroom();

    void updateClassroom();

    List<StudyWeek> getStudyWeek();

    void updateStudyWeek();

    List<WeekDay> getWeekDay();

    void updateWeekDay();

    List<Schedule> searchSchedule(FilterScheduleDto requestDto);

    String calculateTimePerformance(FilterScheduleDto requestDto);
}
