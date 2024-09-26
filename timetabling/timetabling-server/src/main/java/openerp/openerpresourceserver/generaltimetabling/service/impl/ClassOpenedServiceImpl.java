package openerp.openerpresourceserver.generaltimetabling.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import openerp.openerpresourceserver.generaltimetabling.common.CommonUtil;
import openerp.openerpresourceserver.generaltimetabling.exception.*;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.AutoMakeScheduleDto;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.FilterClassOpenedDto;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.MakeScheduleDto;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.UpdateClassOpenedDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassOpened;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Classroom;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Group;
import openerp.openerpresourceserver.generaltimetabling.repo.ClassOpenedRepo;
import openerp.openerpresourceserver.generaltimetabling.repo.ClassroomRepo;
import openerp.openerpresourceserver.generaltimetabling.repo.GroupRepo;
import openerp.openerpresourceserver.generaltimetabling.service.ClassOpenedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ClassOpenedServiceImpl implements ClassOpenedService {

    @Autowired
    private ClassOpenedRepo classOpenedRepo;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private GroupRepo groupRepo;

    @Autowired
    private ClassroomRepo classroomRepo;

    public static final Long MAX_PERIOD = 6L;

    public static final Long CLASS_ENABLE_SEPARATE = 4L;

    public static final String DEFAULT_START_PERIOD = "1";

    public static final Integer MAX_CLASS_FOR_CLASS_OPENED = 2;

    @Override
    public List<ClassOpened> getAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return classOpenedRepo.findAll(sort);
    }

    @Override
    public List<ClassOpened> updateClassOpenedList(UpdateClassOpenedDto requestDto) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        List<ClassOpened> classOpenedList = classOpenedRepo.getAllByIdIn(requestDto.getIds(), sort);
        String groupName = requestDto.getGroupName();
        if (groupName == null) {
            throw new GroupNotFoundException("Không tìm được nhóm!");
        }
        classOpenedList.forEach(el -> {
            el.setGroupName(groupName);
        });
        classOpenedRepo.saveAll(classOpenedList);
        return classOpenedRepo.findAllById(requestDto.getIds());
    }

    @Override
    public List<ClassOpened> getBySemester(String semester) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return classOpenedRepo.getAllBySemester(semester, sort);
    }

    @Override
    public List<ClassOpened> searchClassOpened(FilterClassOpenedDto searchDto) {
        StringBuilder jpql = this.getStringBuilder(searchDto);

        Query query = entityManager.createQuery(jpql.toString());

        // Execute the query and return the result list
        return CommonUtil.buildQueryForClassOpened(query, searchDto).getResultList();
    }

    private StringBuilder getStringBuilder(FilterClassOpenedDto searchDto) {
        StringBuilder jpql = new StringBuilder("SELECT s FROM ClassOpened s WHERE 1 = 1");

        return CommonUtil.appendAttributesForClassOpened(jpql, searchDto);
    }

    @Override
    public void setSeparateClass(Long id, Boolean isSeparateClass) {
        ClassOpened classOpened = classOpenedRepo.findById(id).orElse(null);
        if (classOpened == null) {
            return;
        }
        long totalPeriod = this.calculateTotalPeriod(classOpened.getMass());
        if (isSeparateClass && totalPeriod != CLASS_ENABLE_SEPARATE) {
            throw new UnableSeparateClassException("Chỉ chia lớp có 4 tiết!");
        }
        classOpened.setIsSeparateClass(isSeparateClass);

        classOpened.setClassroom(null);
        classOpened.setStartPeriod(null);
        classOpened.setWeekday(null);
        classOpened.setSecondClassroom(null);
        classOpened.setSecondStartPeriod(null);
        classOpened.setSecondWeekday(null);

        classOpenedRepo.save(classOpened);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        ids.forEach(el -> {
            classOpenedRepo.deleteById(el);
        });
    }

    @Override
    public void makeSchedule(MakeScheduleDto requestDto) {
        ClassOpened classOpened = classOpenedRepo.findById(requestDto.getId()).orElse(null);
        if (classOpened == null) {
            return;
        }

        //Get the main class detail information
        String startPeriod = requestDto.getStartPeriod();
        String weekday = requestDto.getWeekday();
        String classroom = requestDto.getClassroom();
        String subStartPeriod = startPeriod != null ? startPeriod : classOpened.getStartPeriod();
        String subClassroom = classroom != null ? classroom : classOpened.getClassroom();
        String subWeekday = weekday != null ? weekday : classOpened.getWeekday();

        //Get the second class detail information
        String secondStartPeriod = requestDto.getSecondStartPeriod();
        String secondWeekday = requestDto.getSecondWeekday();
        String secondClassroom = requestDto.getSecondClassroom();
        String secondSubStartPeriod = secondStartPeriod != null ? secondStartPeriod : classOpened.getSecondStartPeriod();
        String secondSubClassroom = secondClassroom != null ? secondClassroom : classOpened.getSecondClassroom();
        String secondSubWeekday = secondWeekday != null ? secondWeekday : classOpened.getSecondWeekday();

        // Check conflict for this class
        if (subStartPeriod != null && subClassroom != null && subWeekday != null) {
            this.checkConflictSchedule(classOpened, requestDto);
        }
        if (secondSubStartPeriod != null && secondSubClassroom != null && secondSubWeekday != null) {
            this.checkConflictScheduleForSecondClass(classOpened, requestDto);
        }

        classOpened.setStartPeriod(subStartPeriod);
        classOpened.setWeekday(subWeekday);
        classOpened.setClassroom(subClassroom);
        classOpened.setSecondStartPeriod(secondSubStartPeriod);
        classOpened.setSecondWeekday(secondSubWeekday);
        classOpened.setSecondClassroom(secondSubClassroom);

        classOpenedRepo.save(classOpened);
    }

    private void checkConflictSchedule(ClassOpened classOpened, MakeScheduleDto requestDto) {
        String classroomOfClass = requestDto.getClassroom() != null ? requestDto.getClassroom() : classOpened.getClassroom();
        String weekdayOfClass = requestDto.getWeekday() != null ? requestDto.getWeekday() : classOpened.getWeekday();
        String mass = classOpened.getMass();
        String crew = classOpened.getCrew();
        String semester = classOpened.getSemester();
        Boolean isSeparateClass = classOpened.getIsSeparateClass() != null ? classOpened.getIsSeparateClass() : false;

        long startPeriod = Long.parseLong(requestDto.getStartPeriod() != null ?
                requestDto.getStartPeriod() : classOpened.getStartPeriod());
        long finishPeriod = this.calculateFinishPeriod(mass, startPeriod, isSeparateClass);

        List<ClassOpened> listClassOpened = classOpenedRepo.
                getAllBySemesterAndClassroomAndWeekdayAndCrewAndStartPeriodIsNotNullAndIdNot
                        (semester, classroomOfClass, weekdayOfClass, crew, requestDto.getId());
        List<ClassOpened> listSecondClassOpened = classOpenedRepo.
                getAllBySemesterAndSecondClassroomAndSecondWeekdayAndCrewAndSecondStartPeriodIsNotNull
                        (semester, classroomOfClass, weekdayOfClass, crew);

        this.checkForFirstListClasses(listClassOpened, startPeriod, finishPeriod);
        this.checkForSecondListClasses(listSecondClassOpened, startPeriod, finishPeriod);
    }

    private void checkConflictScheduleForSecondClass(ClassOpened classOpened, MakeScheduleDto requestDto) {
        String classroomOfClass = requestDto.getSecondClassroom() != null ? requestDto.getSecondClassroom() : classOpened.getSecondClassroom();
        String weekdayOfClass = requestDto.getSecondWeekday() != null ? requestDto.getSecondWeekday() : classOpened.getSecondWeekday();
        String mass = classOpened.getMass();
        String crew = classOpened.getCrew();
        String semester = classOpened.getSemester();
        Boolean isSeparateClass = classOpened.getIsSeparateClass() != null ? classOpened.getIsSeparateClass() : false;

        long startPeriod = Long.parseLong(requestDto.getSecondStartPeriod() != null ?
                requestDto.getSecondStartPeriod() : classOpened.getSecondStartPeriod());
        long finishPeriod = this.calculateFinishPeriod(mass, startPeriod, isSeparateClass);

        List<ClassOpened> listClassOpened = classOpenedRepo
                .getAllBySemesterAndClassroomAndWeekdayAndCrewAndStartPeriodIsNotNull
                        (semester, classroomOfClass, weekdayOfClass, crew);
        List<ClassOpened> listSecondClassOpened = classOpenedRepo.
                getAllBySemesterAndSecondClassroomAndSecondWeekdayAndCrewAndSecondStartPeriodIsNotNullAndIdNot
                        (semester, classroomOfClass, weekdayOfClass, crew, requestDto.getId());

        this.checkForFirstListClasses(listClassOpened, startPeriod, finishPeriod);
        this.checkForSecondListClasses(listSecondClassOpened, startPeriod, finishPeriod);
    }

    private void checkForFirstListClasses(List<ClassOpened> listClassOpened, long startPeriod, long finishPeriod) {
        listClassOpened.forEach(el -> {
            String supMass = el.getMass();
            Boolean isSeparateClassExisted = el.getIsSeparateClass() != null ? el.getIsSeparateClass() : false;
            long existedStartPeriod = Long.parseLong(el.getStartPeriod());
            long existedFinishPeriod = this.calculateFinishPeriod(supMass, existedStartPeriod, isSeparateClassExisted);

            this.compareTimePeriod(startPeriod, finishPeriod, el, existedStartPeriod, existedFinishPeriod);
        });
    }

    private void checkForSecondListClasses(List<ClassOpened> listSecondClassOpened, long startPeriod, long finishPeriod) {
        listSecondClassOpened.forEach(el -> {
            String supMass = el.getMass();
            Boolean isSeparateClassExisted = el.getIsSeparateClass() != null ? el.getIsSeparateClass() : false;
            long existedStartPeriod = Long.parseLong(el.getSecondStartPeriod());
            long existedFinishPeriod = this.calculateFinishPeriod(supMass, existedStartPeriod, isSeparateClassExisted);

            this.compareTimePeriod(startPeriod, finishPeriod, el, existedStartPeriod, existedFinishPeriod);
        });
    }

    private void compareTimePeriod(long startPeriod, long finishPeriod, ClassOpened el,
                                  long existedStartPeriod, long existedFinishPeriod) {
        if (startPeriod > existedStartPeriod) {
            if (startPeriod <= existedFinishPeriod) {
                throw new ConflictScheduleException("Trùng lịch với lớp: " + el.getModuleName());
            }
        } else {
            if (finishPeriod >= existedStartPeriod) {
                throw new ConflictScheduleException("Trùng lịch với lớp: " + el.getModuleName());
            }
        }
    }

    private Long calculateFinishPeriod(String mass, Long startPeriod, Boolean isSeparateClass) {
        long totalPeriod = this.calculateTotalPeriod(mass);
        long finishPeriod = isSeparateClass ? (startPeriod + (totalPeriod / 2) - 1) : startPeriod + totalPeriod - 1;

        if (finishPeriod > MAX_PERIOD) {
            throw new UnableStartPeriodException("Tiết bắt đầu lỗi: tiết " + startPeriod);
        }
        return finishPeriod;
    }

    private Long calculateTotalPeriod(String mass) {
        String pattern = "a\\((\\d+(-\\d+)*)\\)";
        if (Pattern.compile(pattern).matcher(mass).matches()) {
            System.out.println("Pattern matched!");
        } else {
            System.out.println("Pattern not matched!");
        }
        mass.replaceAll("\\s+","");
        //a(b-c-d-e) => b-c-d-e => b,c,d,e => b+c
        //Need validation for pattern.
        String numbersString = mass.trim().substring(2, mass.indexOf(')'));
        String[] numbersArray = numbersString.split("-");
        return Long.parseLong(numbersArray[0]) + Long.parseLong(numbersArray[1]);
    }

    // ----------------Automation make schedule---------------------
    @Override
    public void automationMakeScheduleForCTTT(AutoMakeScheduleDto autoMakeScheduleDto) {
        String semester = autoMakeScheduleDto.getSemester();
        String groupName = autoMakeScheduleDto.getGroupName();
        String weekdayPriority = autoMakeScheduleDto.getWeekdayPriority();
        Boolean isClassroomArranged = autoMakeScheduleDto.getIsClassroomArranged();
        String priorityBuilding = this.getPriorityBuilding(groupName);

        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        List<ClassOpened> listClassMakeSchedule = classOpenedRepo.getAllBySemesterAndGroupName(semester, groupName, sort);
        if (listClassMakeSchedule.isEmpty()) {
            return;
        }

        this.resetTimeBeforeAutoMakeSchedule(listClassMakeSchedule);
        String[] weekdayArray = weekdayPriority.split(",");

        int outOfDay = 0;
        int countClass4Period = 0;

        // Tự động sắp xếp lịch học
        for (ClassOpened elClass : listClassMakeSchedule) {
            long totalPeriodOfClass = this.calculateTotalPeriod(elClass.getMass());
            if (totalPeriodOfClass == CLASS_ENABLE_SEPARATE) {
                countClass4Period++;
                if (countClass4Period % 3 == 2) {
                    elClass.setIsSeparateClass(true);
                    classOpenedRepo.save(elClass);
                }
            }

            int countClassForSeparate = 0;
            for (String elWeekday : weekdayArray) {
                List<ClassOpened> existedClasses = listClassMakeSchedule.stream()
                        .filter(obj -> elWeekday.equals(obj.getWeekday()))
                        .toList();
                List<ClassOpened> existedSecondClasses = listClassMakeSchedule.stream()
                        .filter(obj -> elWeekday.equals(obj.getSecondWeekday()))
                        .toList();
                if (existedClasses.isEmpty() && existedSecondClasses.isEmpty()) {
                    // ngày hôm elWeekday chưa được gán lớp học nào
                    countClassForSeparate = this.setTimeStudyForElClass(elClass, elWeekday, DEFAULT_START_PERIOD, countClassForSeparate);
                    // check thoát khỏi vòng lặp từng ngày trong tuần
                    if (!elClass.getIsSeparateClass() || countClassForSeparate == MAX_CLASS_FOR_CLASS_OPENED) break;
                } else {
                    //Đã có lớp học vào ngày hôm đó
                    long startPeriod = this.calculateStartPeriod(existedClasses, existedSecondClasses);
                    long totalPeriod = this.calculateTotalPeriod(elClass.getMass());
                    long finishPeriod = elClass.getIsSeparateClass() ? (startPeriod + (totalPeriod / 2) - 1) : (startPeriod + totalPeriod - 1);
                    if (finishPeriod > MAX_PERIOD) {
                        continue;
                    }
                    String startPeriodForElClass = startPeriod + "";
                    countClassForSeparate = this.setTimeStudyForElClass(elClass, elWeekday, startPeriodForElClass, countClassForSeparate);
                    //check thoát khỏi vòng lặp từng ngày trong tuần
                    if (!elClass.getIsSeparateClass() || countClassForSeparate == MAX_CLASS_FOR_CLASS_OPENED) break;
                }
            }

            //kiểm tra lớp học đã được gán thời gian học chưa
            //Xảy ra khi tất cả các ngày trong tuần đều đã được gán lớp học
            if (elClass.getStartPeriod() == null) {
                elClass.setStartPeriod(DEFAULT_START_PERIOD);
                elClass.setWeekday(weekdayArray[outOfDay]);
                outOfDay++;
                classOpenedRepo.save(elClass);
            }
        }

        //Tự động sắp xếp phòng khi yêu cầu
        if (isClassroomArranged) {
            this.autoSetClassroom(listClassMakeSchedule, priorityBuilding);
        }
    }

    /**
     * @param listClassMakeSchedule Danh sách các lớp học được gán.
     * @param priorityBuilding Tòa nhà ưu tiên.
     * @implNote
     * Hàm này được sử dụng để tự động gán tòa nhà ưu tiên cho danh sách các lóp
     * 
     */
    private void autoSetClassroom(List<ClassOpened> listClassMakeSchedule, String priorityBuilding) {
        for (ClassOpened elClass : listClassMakeSchedule) {
            Boolean isSeparateClass = elClass.getIsSeparateClass();
            String currentCrew = elClass.getCrew();
            String currentMass = elClass.getMass();
            String semester = elClass.getSemester();
            String quantity = !elClass.getQuantity().isEmpty() ? elClass.getQuantity() : elClass.getQuantityMax();
            long currentStartPeriod = Long.parseLong(elClass.getStartPeriod());
            String currentWeekday = elClass.getWeekday();
            long currentFinish = this.calculateFinishPeriod(currentMass, currentStartPeriod, isSeparateClass);
            long amountStudent = Long.parseLong(quantity.contains(".") ? quantity.substring(0, quantity.indexOf(".")) : quantity);

            List<Classroom> classroomList = priorityBuilding != null ?
                    classroomRepo.findClassroomByBuildingAndQuantityMaxAfter(priorityBuilding, amountStudent) :
                    classroomRepo.findClassroomByQuantityMaxAfter(amountStudent);
            if (classroomList.isEmpty()) {
                throw new NotClassroomSuitableException("Không có phòng học phù hợp cho lớp:" + elClass.getModuleName());
            }

            //Gán phòng học cho lớp học đơn hoặc lớp đầu tiên của lớp tách
            for (Classroom classroom : classroomList) {
                boolean setClassroomDone;
                List<ClassOpened> listClassOpened = classOpenedRepo.
                        getAllBySemesterAndClassroomAndWeekdayAndCrewAndStartPeriodIsNotNullAndIdNot
                                (semester, classroom.getClassroom(), currentWeekday, currentCrew, elClass.getId());
                List<ClassOpened> listSecondClassOpened = classOpenedRepo.
                        getAllBySemesterAndSecondClassroomAndSecondWeekdayAndCrewAndSecondStartPeriodIsNotNull
                                (semester, classroom.getClassroom(), currentWeekday, currentCrew);

                //Kiểm tra trùng lịch với danh sách lớp đơn hoặc lớp thứ nhất của lớp tách
                setClassroomDone = this.checkConflictTimeForListFirstClass(listClassOpened, currentStartPeriod, currentFinish);

                //Kiểm tra trùng lịch với danh sách lớp thứ hai của lớp tách
                setClassroomDone = this.checkConflictTimeForListSecondClass(listSecondClassOpened, currentStartPeriod, currentFinish) && setClassroomDone;

                if (setClassroomDone) {
                    elClass.setClassroom(classroom.getClassroom());
                    classOpenedRepo.save(elClass);
                    break;
                }
            }

            //Gán phòng học cho lớp học thứ hai của lớp tách
            if (isSeparateClass) {
                currentStartPeriod = Long.parseLong(elClass.getSecondStartPeriod());
                currentWeekday = elClass.getSecondWeekday();
                currentFinish = this.calculateFinishPeriod(currentMass, currentStartPeriod, isSeparateClass);
                for (Classroom classroom : classroomList) {
                    boolean setClassroomDone;
                    List<ClassOpened> listClassOpened = classOpenedRepo.
                            getAllBySemesterAndClassroomAndWeekdayAndCrewAndStartPeriodIsNotNull
                                    (semester, classroom.getClassroom(), currentWeekday, currentCrew);
                    List<ClassOpened> listSecondClassOpened = classOpenedRepo.
                            getAllBySemesterAndSecondClassroomAndSecondWeekdayAndCrewAndSecondStartPeriodIsNotNullAndIdNot
                                    (semester, classroom.getClassroom(), currentWeekday, currentCrew, elClass.getId());

                    //Kiểm tra trùng lịch với danh sách lớp đơn hoặc lớp thứ nhất của lớp tách
                    setClassroomDone = this.checkConflictTimeForListFirstClass(listClassOpened, currentStartPeriod, currentFinish);

                    //Kiểm tra trùng lịch với danh sách lớp thứ hai của lớp tách
                    setClassroomDone = this.checkConflictTimeForListSecondClass(listSecondClassOpened, currentStartPeriod, currentFinish) && setClassroomDone;

                    if (setClassroomDone) {
                        elClass.setSecondClassroom(classroom.getClassroom());
                        classOpenedRepo.save(elClass);
                        break;
                    }
                }
            }

            //kiểm tra đã gán được lớp hay chưa
            if (elClass.getClassroom() == null) {
                throw new NotClassroomSuitableException("Không có phòng học phù hợp cho lớp:" + elClass.getModuleName());
            } else {
                if (isSeparateClass && elClass.getSecondClassroom() == null) {
                    throw new NotClassroomSuitableException("Không có phòng học phù hợp cho lớp:" + elClass.getModuleName());
                }
            }
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

    private Integer setTimeStudyForElClass(ClassOpened elClass, String elWeekday,
                                          String startPeriod, int countClassForSeparate) {
        if (elClass.getIsSeparateClass()) {
            //Nếu tách lớp
            switch (countClassForSeparate) {
                case 0: //Lớp tách chưa được gán
                    elClass.setWeekday(elWeekday);
                    elClass.setStartPeriod(startPeriod);
                    break;
                case 1: //Lớp tách đã được gán 1 lớp
                    elClass.setSecondWeekday(elWeekday);
                    elClass.setSecondStartPeriod(startPeriod);
                    break;
                default:
                    break;
            }
            countClassForSeparate++;
        } else {
            elClass.setWeekday(elWeekday);
            elClass.setStartPeriod(startPeriod);
        }
        classOpenedRepo.save(elClass);
        return countClassForSeparate;
    }

    /**
     * @param existedClasses .Các lớp không tách và lớp tách
     * @param existedSecondClasses .Các lớp tách kíp sau
     * @return 
     * Hàm trả về tiết đầu tiên thỏa mãn trong danh sách lịch học giữa các lớp trong 1 ngày.
     */
    private Long calculateStartPeriod(List<ClassOpened> existedClasses, List<ClassOpened> existedSecondClasses) {
        //minStartPeriod: Tiết học sớm nhất.
        //sumTotalPeriod: Tổng cộng số tiết học        
        long minStartPeriod = 1;
        long sumTotalPeriod = 0;

        for (ClassOpened classOpened : existedClasses) {
            long startPeriod = Long.parseLong(classOpened.getStartPeriod());
            if (minStartPeriod >= startPeriod) {
                minStartPeriod = startPeriod;
            }
            long totalPeriod = this.calculateTotalPeriod(classOpened.getMass());
            sumTotalPeriod += classOpened.getIsSeparateClass() ? (totalPeriod / 2) : totalPeriod;
        }

        for (ClassOpened classOpened : existedSecondClasses) {
            long secondStartPeriod = Long.parseLong(classOpened.getSecondStartPeriod());
            if (minStartPeriod >= secondStartPeriod) {
                minStartPeriod = secondStartPeriod;
            }
            long totalPeriod = this.calculateTotalPeriod(classOpened.getMass());
            sumTotalPeriod += totalPeriod / 2;
        }

        return minStartPeriod + sumTotalPeriod;
    }

    private String getPriorityBuilding(String groupName) {
        List<Group> groupList = groupRepo.getAllByGroupName(groupName);
        if (!groupList.isEmpty()) {
            return groupList.get(0).getPriorityBuilding();
        }
        return null;
    }

    private void resetTimeBeforeAutoMakeSchedule(List<ClassOpened> classOpenedList) {
        classOpenedList.forEach(el -> {
            el.setWeekday(null);
            el.setClassroom(null);
            el.setStartPeriod(null);
            el.setSecondWeekday(null);
            el.setSecondClassroom(null);
            el.setSecondStartPeriod(null);
            classOpenedRepo.save(el);
        });
    }

    public void autoMakeGeneralSchedule(AutoMakeScheduleDto autoMakeScheduleDto) {
        int maxPeriod = 12;

        String semester = autoMakeScheduleDto.getSemester();
        String groupName = autoMakeScheduleDto.getGroupName();
        String weekdayPriority = autoMakeScheduleDto.getWeekdayPriority();
        Boolean isClassroomArranged = autoMakeScheduleDto.getIsClassroomArranged();
        String priorityBuilding = this.getPriorityBuilding(groupName);

        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        List<ClassOpened> classOpenedList = classOpenedRepo.getAllBySemesterAndGroupName(semester, groupName, sort);
        if (classOpenedList.isEmpty()) {
            return;
        }
        int[] weekDays = Arrays
                .stream(weekdayPriority.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
        for(ClassOpened classOpened : classOpenedList) {
            long courseLength = calculateTotalPeriod(classOpened.getMass());
            String crew = classOpened.getCrew();
            List<Classroom> classroomList = classroomRepo
                    .findClassroomByBuildingAndQuantityMaxAfter(
                            priorityBuilding, Long.valueOf(classOpened.getQuantityMax()));
            for (int day : weekDays) {
                for (int curPeriod = 1; curPeriod<= 12; curPeriod++) {
                    /*Check if the course is separable*/
                    if(courseLength == 4 && curPeriod % 6 == 2 && !classOpened.getIsSeparateClass()) {
                        long startPeriod = curPeriod;
                        long endPeriod = calculateFinishPeriod(classOpened.getMass(), startPeriod, true);
                        /*Check if classList doesnt have any class at that time*/
                        if(!checkTimeConflict(classOpenedList, startPeriod, endPeriod, day, crew)) {
                            for (Classroom classroom : classroomList) {
                                /*Check if at that time, the room is empty*/
                                if (!checkRoomConflict(classOpenedList, classroom, startPeriod, endPeriod, day, crew)) {
                                    classOpened.setStartPeriod(String.valueOf(startPeriod));
                                    classOpened.setWeekday(String.valueOf(day));
                                    classOpened.setClassroom(classroom.getClassroom());
                                    classOpened.setIsSeparateClass(true);

                                    break;
                                }
                            }
                        }
                    } else if(classOpened.getIsSeparateClass()
                            && classOpened.getSecondClassroom() == null
                            && classOpened.getSecondStartPeriod() == null
                            && classOpened.getSecondWeekday() == null
                    ) {
                        if(Integer.parseInt(classOpened.getWeekday()) != day) {
                            long seperateStartPeriod = curPeriod;
                            long seperateEndPeriod = calculateFinishPeriod(classOpened.getMass(), seperateStartPeriod, true);
                            for (Classroom classroom : classroomList) {
                                /*Check if at that time, the room is empty*/
                                if(!checkRoomConflict(classOpenedList, classroom, seperateStartPeriod, seperateEndPeriod, day, crew)) {
                                    classOpened.setStartPeriod(String.valueOf(seperateStartPeriod));
                                    classOpened.setWeekday(String.valueOf(day));
                                    classOpened.setClassroom(classroom.getClassroom());
                                    classOpened.setIsSeparateClass(true);

                                    break;
                                }
                            }
                            break;
                        }

                    } else {
                        long startPeriod = curPeriod;
                        long endPeriod = calculateFinishPeriod(classOpened.getMass(), startPeriod, false);
                        /*Check if classList doesnt have any class at that time*/
                        if(!checkTimeConflict(classOpenedList, startPeriod, endPeriod, day, crew)) {
                            for (Classroom classroom : classroomList) {
                                /*Check if at that time, the room is empty*/
                                if (!checkRoomConflict(classOpenedList, classroom, startPeriod, endPeriod, day, crew)) {
                                    classOpened.setStartPeriod(String.valueOf(startPeriod));
                                    classOpened.setWeekday(String.valueOf(day));
                                    classOpened.setClassroom(classroom.getClassroom());
                                    classOpened.setIsSeparateClass(true);

                                    break;
                                }
                            }
                        }
                    }
                }
            }
            /*If the classOpened info is enough
            then save to database*/
            if(classOpened.getIsEnoughTimeTableInfo()) {
                classOpenedRepo.save(classOpened);
            }
        }
    }

    private boolean checkTimeConflict(List<ClassOpened> classOpenedList, long startPeriod, long endPeriod, int day, String crew) {
        
        return false;
    }

    private boolean checkRoomConflict(List<ClassOpened> classOpenedList, Classroom classroom, long startPeriod, long endPeriod, int day, String crew) {
        return false;
    }
}
