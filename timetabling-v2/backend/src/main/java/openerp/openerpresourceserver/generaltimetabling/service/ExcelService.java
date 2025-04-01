package openerp.openerpresourceserver.generaltimetabling.service;

import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.generaltimetabling.exception.NotFoundException;
import openerp.openerpresourceserver.generaltimetabling.helper.*;
import openerp.openerpresourceserver.generaltimetabling.model.dto.MakeGeneralClassRequest;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.PlanGeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.RoomOccupation;
import openerp.openerpresourceserver.generaltimetabling.repo.*;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.FilterClassOpenedDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassOpened;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Classroom;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Schedule;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.service.impl.PlanGeneralClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ExcelService {

    public static final String CONFLICT_CLASS = "Trùng lịch với lớp: ";

    @Autowired
    private ScheduleRepo scheduleRepo;

    @Autowired
    private ClassOpenedRepo classOpenedRepo;

    @Autowired
    private GeneralClassRepository gcoRepo;

    @Autowired
    private ClassroomRepo classroomRepo;

    @Autowired
    private RoomOccupationService roomOccupationService;

    @Autowired
    private PlanGeneralClassRepository planGeneralClassRepository;

    @Autowired
    private PlanGeneralClassService planGeneralClassService;

    public InputStream exportGeneralExcel(String semester) {
        List<GeneralClass> classes = gcoRepo.findAllBySemester(semester)
                .stream()
                .filter(c -> c.getClassCode() != null && !c.getClassCode().isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));
        classes.sort((a, b) -> {
            Comparable fieldValueA = a.getClassCode();
            Comparable fieldValueB = b.getClassCode();
            return fieldValueA.compareTo(fieldValueB);
        });
        if (classes.isEmpty()) throw new NotFoundException("Kỳ học không có bất kỳ lớp học nào!");
        return GeneralExcelHelper.convertGeneralClassToExcel(classes);
    }


    // public ByteArrayInputStream load() {
    // List<Schedule> schedules = this.getAllSchedules();
    // ByteArrayInputStream in = ExcelHelper.schedulesToExcel(schedules);
    // return in;
    // }

    public ByteArrayInputStream loadExport(FilterClassOpenedDto requestDto) {
        String semester = requestDto.getSemester();
        String groupName = requestDto.getGroupName();
        List<ClassOpened> classOpenedList;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        if (semester != null) {
            if (groupName != null) {
                classOpenedList = classOpenedRepo.getAllBySemesterAndGroupName(semester, groupName, sort);
            } else
                classOpenedList = classOpenedRepo.getAllBySemester(semester, sort);
        } else {
            if (groupName != null) {
                classOpenedList = classOpenedRepo.getAllByGroupName(groupName, sort);
            } else
                classOpenedList = classOpenedRepo.findAll(sort);
        }
        ByteArrayInputStream in = ExcelHelper.classOpenedToExcelExport(classOpenedList);
        return in;
    }

    public ByteArrayInputStream loadClassConflict(List<ClassOpened> classOpenedList) {
        return ExcelHelper.classOpenedToExcelExport(classOpenedList);
    }

    // public void save(MultipartFile file) {
    // try {
    // List<Schedule> tutorials =
    // ExcelHelper.excelToSchedules(file.getInputStream());
    // tutorials.forEach(el -> {
    // if (el != null && !el.getState().equals("Huỷ lớp")) {
    // scheduleRepo.save(el);
    // }
    // });
    // } catch (IOException e) {
    // throw new RuntimeException("fail to store excel data: " + e.getMessage());
    // }
    // }
    @Transactional
    public List<GeneralClass> saveGeneralClasses(MultipartFile file, String semester) {
        gcoRepo.deleteAllBySemester(semester);
        try {
            List<GeneralClass> generalClassList = GeneralExcelHelper
                    .convertFromExcelToGeneralClassOpened(file.getInputStream(), semester);
            if (generalClassList == null) {
                return new ArrayList<>();
            }
            List<RoomOccupation> roomOccupations = new ArrayList<>();
            generalClassList.forEach(generalClass -> {
                generalClass.getTimeSlots().forEach(timeSlot -> {
                    List<String> learningWeekStrings = Arrays.asList(generalClass.getLearningWeeks().trim().split(","));
                    learningWeekStrings.forEach(learningWeek -> {
                        if(
                            LearningWeekValidator.isCorrectFormat(learningWeek)
                        ) {
                            LearningWeekExtractor.extract(learningWeek).forEach(weekInt->{
                                roomOccupations.add(new RoomOccupation(
                                    timeSlot.getRoom(),
                                    generalClass.getClassCode(),
                                    timeSlot.getStartTime(),
                                    timeSlot.getEndTime(),
                                    generalClass.getCrew(),
                                    timeSlot.getWeekday(),
                                    weekInt,
                                    "study",
                                    generalClass.getSemester()
                                ));
                            });
                        }
                    });
                });
            });
            gcoRepo.saveAll(generalClassList);
            roomOccupationService.saveAll(roomOccupations);
            return generalClassList;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<GeneralClass>();
        }
    }

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
                        long currentFinish = this.calculateFinishPeriod(el.getMass(), startPeriod,
                                el.getIsSeparateClass());

                        String conflictClass = null;
                        String conflictSecondClass = null;
                        List<ClassOpened> existedClassOpened = classOpenedRepo
                                .getAllBySemesterAndClassroomAndWeekdayAndCrewAndStartPeriodIsNotNull(semester,
                                        classroom, weekday, crew);
                        List<ClassOpened> existedClassOpenedSecond = classOpenedRepo
                                .getAllBySemesterAndSecondClassroomAndSecondWeekdayAndCrewAndSecondStartPeriodIsNotNull(
                                        semester, classroom, weekday, crew);

                        // Kiểm tra trùng lịch với danh sách lớp đơn hoặc lớp thứ nhất của lớp tách
                        conflictClass = this.checkConflictTimeForListFirstClass(existedClassOpened, startPeriod,
                                currentFinish);

                        // Kiểm tra trùng lịch với danh sách lớp thứ hai của lớp tách
                        conflictSecondClass = this.checkConflictTimeForListSecondClass(existedClassOpenedSecond,
                                startPeriod, currentFinish);

                        if (conflictClass != null || conflictSecondClass != null) {
                            classOpenedConflictList.add(el);
                            String stateConflict = conflictClass != null ? conflictClass + ","
                                    : "" + " "
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
                            currentFinish = this.calculateFinishPeriod(el.getMass(), startPeriod,
                                    el.getIsSeparateClass());
                            String conflictClassSecond = null;
                            String conflictSecondClassSecond = null;
                            List<ClassOpened> listClassOpened = classOpenedRepo
                                    .getAllBySemesterAndClassroomAndWeekdayAndCrewAndStartPeriodIsNotNull(semester,
                                            classroom, weekday, crew);
                            List<ClassOpened> listSecondClassOpened = classOpenedRepo
                                    .getAllBySemesterAndSecondClassroomAndSecondWeekdayAndCrewAndSecondStartPeriodIsNotNullAndIdNot(
                                            semester, classroom, weekday, crew, el.getId());

                            // Kiểm tra trùng lịch với danh sách lớp đơn hoặc lớp thứ nhất của lớp tách
                            conflictClassSecond = this.checkConflictTimeForListFirstClass(listClassOpened, startPeriod,
                                    currentFinish);

                            // Kiểm tra trùng lịch với danh sách lớp thứ hai của lớp tách
                            conflictSecondClassSecond = this.checkConflictTimeForListSecondClass(listSecondClassOpened,
                                    startPeriod, currentFinish);

                            if (conflictClassSecond != null || conflictSecondClassSecond != null) {
                                boolean isExisted = classOpenedConflictList.contains(el);
                                String stateConflict = conflictClassSecond != null ? conflictClassSecond + ","
                                        : "" + " "
                                                + conflictSecondClassSecond != null ? conflictSecondClassSecond + ","
                                                        : "";
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
                    } else
                        classOpenedRepo.save(el);

                    // el.setSemester(semester);
                    // classOpenedRepo.save(el);
                }
            }
            return classOpenedConflictList;
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

    private String checkConflictTimeForListFirstClass(List<ClassOpened> listClassOpened, long currentStartPeriod,
            long currentFinish) {
        for (ClassOpened el : listClassOpened) {
            String supMass = el.getMass();
            String moduleName = el.getModuleName();
            Boolean isSeparateClassExisted = el.getIsSeparateClass() != null ? el.getIsSeparateClass() : false;
            long existedStartPeriod = Long.parseLong(el.getStartPeriod());
            long existedFinishPeriod = this.calculateFinishPeriod(supMass, existedStartPeriod, isSeparateClassExisted);

            if (!this.compareTimeForSetClassroom(currentStartPeriod, currentFinish, existedStartPeriod,
                    existedFinishPeriod)) {
                return moduleName;
            }
        }
        return null;
    }

    private String checkConflictTimeForListSecondClass(List<ClassOpened> listSecondClassOpened, long currentStartPeriod,
            long currentFinish) {
        for (ClassOpened el : listSecondClassOpened) {
            String supMass = el.getMass();
            String moduleName = el.getModuleName();
            Boolean isSeparateClassExisted = el.getIsSeparateClass() != null ? el.getIsSeparateClass() : false;
            long existedStartPeriod = Long.parseLong(el.getSecondStartPeriod());
            long existedFinishPeriod = this.calculateFinishPeriod(supMass, existedStartPeriod, isSeparateClassExisted);

            if (!this.compareTimeForSetClassroom(currentStartPeriod, currentFinish, existedStartPeriod,
                    existedFinishPeriod)) {
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
        // a(b-c-d-e) => b-c-d-e => b,c,d,e => b+c
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

    public ByteArrayInputStream exportRoomOccupationExcel(String semester, int week) {
        return roomOccupationService.exportExcel(semester, week);
    }


    @Transactional
    public List<PlanGeneralClass> savePlanClasses(MultipartFile file, String semester, boolean createClass) {
        try {
            //planGeneralClassRepository.deleteAllBySemester(semester);
            List<PlanGeneralClass> planClasses = PlanGeneralClassExcelHelper
                    .convertExcelToPlanGeneralClasses(file.getInputStream(), semester);
            //return planGeneralClassRepository.saveAll(planClasses);
            planGeneralClassRepository.saveAll(planClasses);
            if(createClass){
                // create classes from planClasses
                for(PlanGeneralClass p: planClasses) {
                    for(int i = 1;i <= p.getNumberOfClasses();i++) {
                        MakeGeneralClassRequest req = new MakeGeneralClassRequest();
                        req.setId(p.getId());
                        req.setNbClasses(p.getNumberOfClasses());
                        req.setClassType(p.getClassType());
                        req.setDuration(p.getDuration());
                        req.setCrew(p.getCrew());
                        req.setMass(p.getMass());
                        req.setLearningWeeks(p.getLearningWeeks());
                        req.setModuleCode(p.getModuleCode());
                        req.setSemester(p.getSemester());
                        req.setModuleName(p.getModuleName());
                        req.setExerciseMaxQuantity(p.getExerciseMaxQuantity());
                        req.setLectureMaxQuantity(p.getLectureMaxQuantity());
                        req.setLectureExerciseMaxQuantity(p.getLectureExerciseMaxQuantity());
                        req.setProgramName(p.getProgramName());
                        req.setWeekType(p.getWeekType());
                        planGeneralClassService.makeClass(req);
                    }
                }
            }
            planClasses = planGeneralClassRepository.findAllBySemester(semester);
            return planClasses;
        } catch (IOException e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
    }


}
