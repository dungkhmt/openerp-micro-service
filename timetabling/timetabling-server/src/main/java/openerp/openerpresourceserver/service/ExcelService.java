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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import openerp.openerpresourceserver.helper.ExcelHelper;

@Service
public class ExcelService {

    public static final String CONFLICT_CLASS = "Trùng lịch với lớp: ";

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
                    el.setSemester(semester);
                    if (el.getStartPeriod() != null && el.getClassroom() != null && el.getWeekday() != null) {
                        String crew = el.getCrew();
                        Long startPeriod = Long.parseLong(el.getStartPeriod());
                        String weekday = el.getWeekday();
                        String classroom = el.getClassroom();
                        long currentFinish = this.calculateFinishPeriod(el.getMass(), startPeriod, el.getIsSeparateClass());

                        String conflictClass = null;
                        String conflictSecondClass = null;
                        List<ClassOpened> existedClassOpened = classOpenedRepo
                                .getAllBySemesterAndClassroomAndWeekdayAndCrewAndStartPeriodIsNotNull(semester, classroom, weekday, crew);
                        List<ClassOpened> existedClassOpenedSecond = classOpenedRepo
                                .getAllBySemesterAndSecondClassroomAndSecondWeekdayAndCrewAndSecondStartPeriodIsNotNull(semester, classroom, weekday, crew);

                        //Kiểm tra trùng lịch với danh sách lớp đơn hoặc lớp thứ nhất của lớp tách
                        conflictClass = this.checkConflictTimeForListFirstClass(existedClassOpened, startPeriod, currentFinish);

                        //Kiểm tra trùng lịch với danh sách lớp thứ hai của lớp tách
                        conflictSecondClass = this.checkConflictTimeForListSecondClass(existedClassOpenedSecond, startPeriod, currentFinish);

                        if (conflictClass != null || conflictSecondClass != null ) {
                            classOpenedConflictList.add(el);
                            String stateConflict =
                                    conflictClass != null ? conflictClass + "," : "" + " "
                                    + conflictSecondClass != null ? conflictSecondClass + "," : "";
                            el.setState(CONFLICT_CLASS + stateConflict);
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
                            String conflictClassSecond = null;
                            String conflictSecondClassSecond = null;
                            List<ClassOpened> listClassOpened = classOpenedRepo.
                                    getAllBySemesterAndClassroomAndWeekdayAndCrewAndStartPeriodIsNotNull
                                            (semester, classroom, weekday, crew);
                            List<ClassOpened> listSecondClassOpened = classOpenedRepo.
                                    getAllBySemesterAndSecondClassroomAndSecondWeekdayAndCrewAndSecondStartPeriodIsNotNullAndIdNot
                                            (semester, classroom, weekday, crew, el.getId());

                            //Kiểm tra trùng lịch với danh sách lớp đơn hoặc lớp thứ nhất của lớp tách
                            conflictClassSecond = this.checkConflictTimeForListFirstClass(listClassOpened, startPeriod, currentFinish);

                            //Kiểm tra trùng lịch với danh sách lớp thứ hai của lớp tách
                            conflictSecondClassSecond = this.checkConflictTimeForListSecondClass(listSecondClassOpened, startPeriod, currentFinish);

                            if (conflictClassSecond != null || conflictSecondClassSecond != null) {
                                boolean isExisted = classOpenedConflictList.contains(el);
                                String stateConflict =
                                        conflictClassSecond != null ? conflictClassSecond + "," : "" + " "
                                                + conflictSecondClassSecond != null ? conflictSecondClassSecond + "," : "";
                                if (isExisted) {
                                    String stateExisted = el.getState();
                                    el.setState(stateExisted + " " + stateConflict);
                                } else {
                                    classOpenedConflictList.add(el);
                                    el.setState(CONFLICT_CLASS + stateConflict);
                                }
                                el.setSecondClassroom(null);
                                el.setSecondStartPeriod(null);
                                el.setSecondWeekday(null);
                            }
                            classOpenedRepo.save(el);
                        }
                    } else classOpenedRepo.save(el);

//                    el.setSemester(semester);
//                    classOpenedRepo.save(el);
                }
            }
            return classOpenedConflictList;
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

    private String checkConflictTimeForListFirstClass(List<ClassOpened> listClassOpened, long currentStartPeriod, long currentFinish) {
        for (ClassOpened el : listClassOpened) {
            String supMass = el.getMass();
            String moduleName = el.getModuleName();
            Boolean isSeparateClassExisted = el.getIsSeparateClass() != null ? el.getIsSeparateClass() : false;
            long existedStartPeriod = Long.parseLong(el.getStartPeriod());
            long existedFinishPeriod = this.calculateFinishPeriod(supMass, existedStartPeriod, isSeparateClassExisted);

            if (!this.compareTimeForSetClassroom(currentStartPeriod, currentFinish, existedStartPeriod, existedFinishPeriod)) {
                return moduleName;
            }
        }
        return null;
    }

    private String checkConflictTimeForListSecondClass(List<ClassOpened> listSecondClassOpened, long currentStartPeriod, long currentFinish) {
        for (ClassOpened el : listSecondClassOpened) {
            String supMass = el.getMass();
            String moduleName = el.getModuleName();
            Boolean isSeparateClassExisted = el.getIsSeparateClass() != null ? el.getIsSeparateClass() : false;
            long existedStartPeriod = Long.parseLong(el.getSecondStartPeriod());
            long existedFinishPeriod = this.calculateFinishPeriod(supMass, existedStartPeriod, isSeparateClassExisted);

            if (!this.compareTimeForSetClassroom(currentStartPeriod, currentFinish, existedStartPeriod, existedFinishPeriod)) {
                return moduleName;
            }
        }
        return null;
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
        String numbersString = mass.trim().substring(2, mass.indexOf(')'));
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
