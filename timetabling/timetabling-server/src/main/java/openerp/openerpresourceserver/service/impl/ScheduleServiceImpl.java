package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.common.CommonUtil;
import openerp.openerpresourceserver.model.dto.request.FilterScheduleDto;
import openerp.openerpresourceserver.model.entity.*;
import openerp.openerpresourceserver.repo.*;
import openerp.openerpresourceserver.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    public static final Integer MAX_CREW_PER_DAY = 12;

    @Autowired
    private ScheduleRepo scheduleRepo;

    @Autowired
    private SemesterRepo semesterRepo;

    @Autowired
    private InstituteRepo instituteRepo;

    @Autowired
    private ClassCodeRepo classCodeRepo;

    @Autowired
    private ClassroomRepo classroomRepo;

    @Autowired
    private StudyWeekRepo studyWeekRepo;

    @Autowired
    private WeekDayRepo weekDayRepo;

    @Override
    public List<Semester> getSemester() {
        return semesterRepo.findAll();
    }

    @Override
    public void updateSemester() {
        List<String> semesterDataList = scheduleRepo.getSemester();
        if (!semesterDataList.isEmpty()) {
            semesterRepo.deleteAll();
        }
        List<Semester> semesterList = new ArrayList<>();
        semesterDataList.forEach(el -> {
            Semester semester = Semester.builder()
                    .semester(el)
                    .build();
            semesterList.add(semester);
        });
        semesterRepo.saveAll(semesterList);
    }

    @Override
    public List<Institute> getInstitute() {
        return instituteRepo.findAll();
    }

    @Override
    public void updateInstitute() {
        List<String> instituteDataList = scheduleRepo.getInstitute();
        if (!instituteDataList.isEmpty()) {
            instituteRepo.deleteAll();
        }
        List<Institute> instituteList = new ArrayList<>();
        instituteDataList.forEach(el -> {
            Institute institute = Institute.builder()
                    .institute(el)
                    .build();
            instituteList.add(institute);
        });
        instituteRepo.saveAll(instituteList);
    }

    @Override
    public List<ClassCode> getClassCode() {
        return classCodeRepo.findAll();
    }

    @Override
    public void updateClassCode() {
        List<String> classCodeDataList = scheduleRepo.getClassCode();
        if (!classCodeDataList.isEmpty()) {
            classCodeRepo.deleteAll();
        }
        List<ClassCode> classCodeList = new ArrayList<>();
        classCodeDataList.forEach(el -> {
            String[] classcode = el.split(",");
            ClassCode classCode = ClassCode.builder()
                    .classCode(classcode[0])
                    .bundleClassCode(classcode[1])
                    .build();
            classCodeList.add(classCode);
        });
        classCodeRepo.saveAll(classCodeList);
    }

    @Override
    public List<Classroom> getClassroom() {
        return classroomRepo.findAll();
    }

    @Override
    public void updateClassroom() {
        List<String> classroomDataList = scheduleRepo.getClassroom();
        if (!classroomDataList.isEmpty()) {
            classroomRepo.deleteAll();
        }
        List<Classroom> classroomList = new ArrayList<>();
        classroomDataList.forEach(el -> {
            Classroom classroom = Classroom.builder()
                    .classroom(el)
                    .build();
            classroomList.add(classroom);
        });
        classroomRepo.saveAll(classroomList);
    }

    @Override
    public List<StudyWeek> getStudyWeek() {
        return studyWeekRepo.findAll();
    }

    @Override
    public void updateStudyWeek() {
        List<String> studyWeekDataList = scheduleRepo.getStudyWeek();
        if (!studyWeekDataList.isEmpty()) {
            studyWeekRepo.deleteAll();
        }
        List<StudyWeek> studyWeekList = new ArrayList<>();
        studyWeekDataList.forEach(el -> {
            StudyWeek studyWeek = StudyWeek.builder()
                    .studyweek(el)
                    .build();
            studyWeekList.add(studyWeek);
        });
        studyWeekRepo.saveAll(studyWeekList);
    }

    @Override
    public List<WeekDay> getWeekDay() {
        return weekDayRepo.findAll();
    }

    @Override
    public void updateWeekDay() {
        List<String> weekDayDataList = scheduleRepo.getWeekDay();
        if (!weekDayDataList.isEmpty()) {
            weekDayRepo.deleteAll();
        }
        List<WeekDay> weekDayList = new ArrayList<>();
        weekDayDataList.forEach(el -> {
            WeekDay weekDay = WeekDay.builder()
                    .weekDay(el)
                    .build();
            weekDayList.add(weekDay);
        });
        weekDayRepo.saveAll(weekDayList);
    }

    @Override
    public List<Schedule> searchSchedule(FilterScheduleDto requestDto) {
        return scheduleRepo.getSchedulesByClassRoomAndStudyWeekAndWeekDay(requestDto.getClassRoom(),
                requestDto.getStudyWeek(), requestDto.getWeekDay());
    }

    @Override
    public String calculateTimePerformance(FilterScheduleDto requestDto) {
        List<Schedule> scheduleList = this.searchSchedule(requestDto);
        int totalTimeUsing = 0;
        for (int i = 0; i < scheduleList.size(); i++) {
            Schedule schedule = scheduleList.get(i);
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
