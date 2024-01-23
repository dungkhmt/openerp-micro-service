package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.exception.UnableStartPeriodException;
import openerp.openerpresourceserver.model.dto.request.FilterClassOpenedDto;
import openerp.openerpresourceserver.model.entity.ClassOpened;
import openerp.openerpresourceserver.model.entity.Classroom;
import openerp.openerpresourceserver.model.entity.Schedule;
import openerp.openerpresourceserver.repo.ClassOpenedRepo;
import openerp.openerpresourceserver.repo.ClassroomRepo;
import openerp.openerpresourceserver.repo.ScheduleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import openerp.openerpresourceserver.helper.ExcelHelper;

@Service
public class ExcelService {

    public static final String CONFLICT_CLASS = "Lịch học bị trùng";

    public static final String CONFLICT_SECOND_CLASS = "Lịch học của lớp thứ hai bị trùng";

    @Autowired
    private ScheduleRepo scheduleRepo;

    @Autowired
    private ClassOpenedRepo classOpenedRepo;

    @Autowired
    private ClassroomRepo classroomRepo;

//    public ByteArrayInputStream load() {
//        List<Schedule> schedules = this.getAllSchedules();
//        ByteArrayInputStream in = ExcelHelper.schedulesToExcel(schedules);
//        return in;
//    }

    public ByteArrayInputStream loadExport(FilterClassOpenedDto requestDto) {
        String semester = requestDto.getSemester();
        String groupName = requestDto.getGroupName();
        List<ClassOpened> classOpenedList;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        if (semester != null) {
            if (groupName != null) {
                classOpenedList = classOpenedRepo.getAllBySemesterAndGroupName(semester, groupName, sort);
            } else classOpenedList = classOpenedRepo.getAllBySemester(semester, sort);
        } else {
            if (groupName != null) {
                classOpenedList = classOpenedRepo.getAllByGroupName(groupName, sort);
            } else classOpenedList = classOpenedRepo.findAll(sort);
        }
        ByteArrayInputStream in = ExcelHelper.classOpenedToExcelExport(classOpenedList);
        return in;
    }

    public ByteArrayInputStream loadClassConflict(List<ClassOpened> classOpenedList) {
        return ExcelHelper.classOpenedToExcelExport(classOpenedList);
    }

//    public void save(MultipartFile file) {
//        try {
//            List<Schedule> tutorials = ExcelHelper.excelToSchedules(file.getInputStream());
//            tutorials.forEach(el -> {
//                if (el != null && !el.getState().equals("Huỷ lớp")) {
//                    scheduleRepo.save(el);
//                }
//            });
//        } catch (IOException e) {
//            throw new RuntimeException("fail to store excel data: " + e.getMessage());
//        }
//    }

    public List<ClassOpened> saveClassOpened(MultipartFile file, String semester) {
        try {
            List<ClassOpened> tutorials = ExcelHelper.excelToClassOpened(file.getInputStream());
            List<ClassOpened> classOpenedConflictList = new ArrayList<>();
            for (ClassOpened el : tutorials) {
                if (el != null && !el.getCourse().isEmpty() && !el.getStudyClass().isEmpty()) {
                    if (!el.getStartPeriod().isEmpty() && !el.getClassroom().isEmpty() && !el.getWeekday().isEmpty()) {
                        el.setSemester(semester);
                        String crew = el.getCrew();
                        Long startPeriod = Long.parseLong(el.getStartPeriod());
                        String weekday = el.getWeekday();
                        String classroom = el.getClassroom();
                        long currentFinish = this.calculateFinishPeriod(el.getMass(), startPeriod, el.getIsSeparateClass());

                        boolean setClassroomDone;
                        List<ClassOpened> existedClassOpened = classOpenedRepo
                                .getAllBySemesterAndClassroomAndWeekdayAndCrewAndStartPeriodIsNotNull(semester, classroom, weekday, crew);
                        List<ClassOpened> existedClassOpenedSecond = classOpenedRepo
                                .getAllBySemesterAndSecondClassroomAndSecondWeekdayAndCrewAndSecondStartPeriodIsNotNull(semester, classroom, weekday, crew);

                        //Kiểm tra trùng lịch với danh sách lớp đơn hoặc lớp thứ nhất của lớp tách
                        setClassroomDone = this.checkConflictTimeForListFirstClass(existedClassOpened, startPeriod, currentFinish);

                        //Kiểm tra trùng lịch với danh sách lớp thứ hai của lớp tách
                        setClassroomDone = this.checkConflictTimeForListSecondClass(existedClassOpenedSecond, startPeriod, currentFinish) && setClassroomDone;

                        if (!setClassroomDone) {
                            classOpenedConflictList.add(el);
                            el.setState(CONFLICT_CLASS);
                            el.setClassroom(null);
                            el.setWeekday(null);
                            el.setStartPeriod(null);
                        }
                        classOpenedRepo.save(el);

                        if (el.getIsSeparateClass()) {
                            startPeriod = Long.parseLong(el.getSecondStartPeriod());
                            weekday = el.getSecondWeekday();
                            classroom = el.getSecondClassroom();
                            currentFinish = this.calculateFinishPeriod(el.getMass(), startPeriod, el.getIsSeparateClass());
                            boolean setClassroomDoneSecond;
                            List<ClassOpened> listClassOpened = classOpenedRepo.
                                    getAllBySemesterAndClassroomAndWeekdayAndCrewAndStartPeriodIsNotNull
                                            (semester, classroom, weekday, crew);
                            List<ClassOpened> listSecondClassOpened = classOpenedRepo.
                                    getAllBySemesterAndSecondClassroomAndSecondWeekdayAndCrewAndSecondStartPeriodIsNotNullAndIdNot
                                            (semester, classroom, weekday, crew, el.getId());

                            //Kiểm tra trùng lịch với danh sách lớp đơn hoặc lớp thứ nhất của lớp tách
                            setClassroomDoneSecond = this.checkConflictTimeForListFirstClass(listClassOpened, startPeriod, currentFinish);

                            //Kiểm tra trùng lịch với danh sách lớp thứ hai của lớp tách
                            setClassroomDoneSecond = this.checkConflictTimeForListSecondClass(listSecondClassOpened, startPeriod, currentFinish) && setClassroomDoneSecond;

                            if (!setClassroomDoneSecond) {
                                classOpenedConflictList.add(el);
                                el.setState(CONFLICT_SECOND_CLASS);
                                el.setSecondClassroom(null);
                                el.setSecondStartPeriod(null);
                                el.setSecondWeekday(null);
                            }
                            classOpenedRepo.save(el);
                        }
                    } else classOpenedRepo.save(el);
                }
            }
            return classOpenedConflictList;
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

    private Boolean checkConflictTimeForListFirstClass(List<ClassOpened> listClassOpened, long currentStartPeriod, long currentFinish) {
        boolean setClassroomDone = true;
        for (ClassOpened el : listClassOpened) {
            String supMass = el.getMass();
            Boolean isSeparateClassExisted = el.getIsSeparateClass() != null ? el.getIsSeparateClass() : false;
            long existedStartPeriod = Long.parseLong(el.getStartPeriod());
            long existedFinishPeriod = this.calculateFinishPeriod(supMass, existedStartPeriod, isSeparateClassExisted);

            if (!this.compareTimeForSetClassroom(currentStartPeriod, currentFinish, existedStartPeriod, existedFinishPeriod)) {
                setClassroomDone = false;
                break;
            }
        }
        return setClassroomDone;
    }

    private Boolean checkConflictTimeForListSecondClass(List<ClassOpened> listSecondClassOpened, long currentStartPeriod, long currentFinish) {
        boolean setClassroomDone = true;
        for (ClassOpened el : listSecondClassOpened) {
            String supMass = el.getMass();
            Boolean isSeparateClassExisted = el.getIsSeparateClass() != null ? el.getIsSeparateClass() : false;
            long existedStartPeriod = Long.parseLong(el.getSecondStartPeriod());
            long existedFinishPeriod = this.calculateFinishPeriod(supMass, existedStartPeriod, isSeparateClassExisted);

            if (!this.compareTimeForSetClassroom(currentStartPeriod, currentFinish, existedStartPeriod, existedFinishPeriod)) {
                setClassroomDone = false;
            }
        }
        return setClassroomDone;
    }

    private Boolean compareTimeForSetClassroom(long currentStartPeriod, long currentFinish,
                                               long existedStartPeriod, long existedFinishPeriod) {
        if (currentStartPeriod > existedStartPeriod) {
            return currentStartPeriod > existedFinishPeriod;
        } else {
            return currentFinish < existedStartPeriod;
        }
    }

    private Long calculateFinishPeriod(String mass, Long startPeriod, Boolean isSeparateClass) {
        long totalPeriod = this.calculateTotalPeriod(mass);
        long finishPeriod = isSeparateClass ? (startPeriod + (totalPeriod / 2) - 1) : startPeriod + totalPeriod - 1;
        return finishPeriod;
    }

    private Long calculateTotalPeriod(String mass) {
        //a(b-c-d-e) => b-c-d-e => b,c,d,e => b+c
        String numbersString = mass.substring(2, mass.indexOf(')'));
        String[] numbersArray = numbersString.split("-");
        return Long.parseLong(numbersArray[0]) + Long.parseLong(numbersArray[1]);
    }

    public void saveClassroom(MultipartFile file) {
        try {
            List<Classroom> tutorials = ExcelHelper.excelToClassroom(file.getInputStream());
            tutorials.forEach(el -> {
                if (el != null && !el.getClassroom().isEmpty() && el.getQuantityMax() != null) {
                    classroomRepo.save(el);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepo.findAll();
    }
}
