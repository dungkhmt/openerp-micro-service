package openerp.openerpresourceserver.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import openerp.openerpresourceserver.common.CommonUtil;
import openerp.openerpresourceserver.model.dto.request.FilterScheduleDto;
import openerp.openerpresourceserver.model.dto.request.RequestPerformanceDto;
import openerp.openerpresourceserver.model.entity.*;
import openerp.openerpresourceserver.repo.*;
import openerp.openerpresourceserver.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    public static final Integer MAX_CREW_PER_DAY = 12;

    public static final Integer FIRST_WEEK_OF_SEMESTER_1 = 2;

    public static final Integer LAST_WEEK_OF_SEMESTER_1 = 19;

    public static final Integer FIRST_DAY_OF_WEEK = 2;

    public static final Integer LAST_DAY_OF_WEEK = 8;

    @Autowired
    private ScheduleRepo scheduleRepo;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TimePerformanceRepo timePerformanceRepo;

    @Autowired
    private SemesterRepo semesterRepo;

    @Autowired
    private ClassroomRepo classroomRepo;

    //---------------Search for schedule--------------------

    @Override
    public List<Schedule> searchSchedule(FilterScheduleDto searchDto) {
        StringBuilder jpql = this.getStringBuilder(searchDto);

        Query query = entityManager.createQuery(jpql.toString());

        // Execute the query and return the result list
        return CommonUtil.buildQuery(query, searchDto).getResultList();
    }

    private StringBuilder getStringBuilder(FilterScheduleDto searchDto) {
        StringBuilder jpql = new StringBuilder("SELECT s FROM Schedule s WHERE 1 = 1");

        return CommonUtil.appendAttributes(jpql, searchDto);
    }

    //-------------Calculate performance schedule--------------
    public void calculateTimePerformancePerClassroom(FilterScheduleDto requestDto) {
        List<Schedule> scheduleList = this.searchSchedule(requestDto);
        HashMap<Long, Double> performanceList = new HashMap<>();
        //calculate for each schedule
        for (Schedule schedule : scheduleList) {
            double performance = this.calculateTimePerformPerSchedule(schedule);
            performanceList.put(schedule.getId(), performance);
        }
        String semester = requestDto.getSemester();
        int subSemester = Integer.parseInt(semester.substring(4));
        switch (subSemester) {
            case 1:
                List<TimePerformance> resultList = new ArrayList<>();
                //for study week from 2 to 19
                for (int i = FIRST_WEEK_OF_SEMESTER_1; i <= LAST_WEEK_OF_SEMESTER_1; i++) {
                    //for week day from 2 to 8
                    for (int j = FIRST_DAY_OF_WEEK; j <= LAST_DAY_OF_WEEK; j++) {
                        TimePerformance timePerformance = TimePerformance.builder()
                                .studyWeek("" + i)
                                .weekDay("" + j)
                                .classRoom(requestDto.getClassRoom())
                                .semester(requestDto.getSemester())
                                .build();
                        double performanceTime = 0;
                        //get schedules has week day j
                        List<Schedule> scheduleListWithDay = scheduleList.stream()
                                .filter(schedule -> schedule.getWeekDay().equals(timePerformance.getWeekDay()))
                                .toList();
                        //check if schedule has study week i
                        for (Schedule schedule : scheduleListWithDay) {
                            String studyWeek = schedule.getStudyWeek();
                            String[] weeksOfStudy = studyWeek.split(",");
                            for (String week : weeksOfStudy) {
                                if (week.contains("-")) {
                                    String[] weeks = week.replaceAll("\\s", "").split("-");
                                    int start = Integer.parseInt(weeks[0]);
                                    int end = Integer.parseInt(weeks[1]);
                                    if (start <= i && end >= i ) {
                                        performanceTime += performanceList.get(schedule.getId());
                                    }
                                } else if (week.equals(i+"")) {
                                    performanceTime += performanceList.get(schedule.getId());
                                }
                            }
                        }
                        double result = Math.round(performanceTime * 100.0) / 100.0;
                        timePerformance.setPerformanceTime(Math.min(result, 100.0) + " %");
                        resultList.add(timePerformance);
                    }
                }
                timePerformanceRepo.saveAll(resultList);
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }
    }

    @Override
    public String calculateTimePerformancePerDay(List<Schedule> scheduleList) {
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

    @Override
    public void calculateAllTimePerformance() {
        timePerformanceRepo.deleteAll();
        List<Semester> semesterList = semesterRepo.findAll();
        List<Classroom> classroomList = classroomRepo.findAll();
        for (Semester semester : semesterList) {
            for (Classroom classroom : classroomList) {
                FilterScheduleDto filterScheduleDto = FilterScheduleDto.builder()
                        .semester(semester.getSemester())
                        .classRoom(classroom.getClassroom())
                        .build();
                this.calculateTimePerformancePerClassroom(filterScheduleDto);
            }
        }
    }

    public Double calculateTimePerformPerSchedule(Schedule schedule) {
        String start = schedule.getStart();
        String finish = schedule.getFinish();
        int countCrew = CommonUtil.calculateTimeCrew(start, finish);

        return (double) countCrew * 100 / MAX_CREW_PER_DAY;
    }

    @Override
    @Transactional
    public void calculateTimePerformance(RequestPerformanceDto requestDto) {
        timePerformanceRepo.deleteAllByClassRoomAndSemester(requestDto.getClassRoom(), requestDto.getSemester());
        FilterScheduleDto filterScheduleDto = FilterScheduleDto.builder()
                .semester(requestDto.getSemester())
                .classRoom(requestDto.getClassRoom())
                .build();
        this.calculateTimePerformancePerClassroom(filterScheduleDto);
    }

//    public String calculateSeatPerformance(List<Schedule> scheduleList) {
//
//    }
}
