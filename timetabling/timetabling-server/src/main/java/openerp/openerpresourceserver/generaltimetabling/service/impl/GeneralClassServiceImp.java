package openerp.openerpresourceserver.generaltimetabling.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.generaltimetabling.algorithms.V2ClassScheduler;
import openerp.openerpresourceserver.generaltimetabling.exception.ConflictScheduleException;
import openerp.openerpresourceserver.generaltimetabling.exception.NotFoundException;
import openerp.openerpresourceserver.generaltimetabling.helper.ClassTimeComparator;
import openerp.openerpresourceserver.generaltimetabling.helper.GeneralExcelHelper;
import openerp.openerpresourceserver.generaltimetabling.helper.LearningWeekExtractor;
import openerp.openerpresourceserver.generaltimetabling.mapper.RoomOccupationMapper;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.general.UpdateGeneralClassRequest;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.general.UpdateGeneralClassScheduleRequest;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.general.V2UpdateClassScheduleRequest;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Classroom;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Group;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.RoomReservation;
import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.RoomOccupation;
import openerp.openerpresourceserver.generaltimetabling.repo.*;
import openerp.openerpresourceserver.generaltimetabling.service.GeneralClassOpenedService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GeneralClassOpenedServiceImp
 */
@AllArgsConstructor
@Service
@Log4j2
@Slf4j
public class GeneralClassOpenedServiceImp implements GeneralClassOpenedService {

    private GeneralClassOpenedRepository gcoRepo;

    private GroupRepo groupRepo;

    private RoomOccupationRepo roomOccupationRepo;

    private RoomReservationRepo roomReservationRepo;

    private ClassroomRepo classroomRepo;

    private GeneralExcelHelper excelHelper;


    @Override
    public List<GeneralClass> getGeneralClasses(String semester, String groupName) {
        if (groupName == null || groupName.isEmpty()) return gcoRepo.findAllBySemester(semester);
        return gcoRepo.findAllBySemesterAndGroupName(semester, groupName);
    }

    @Override
    public void deleteAllGeneralClasses() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAllGeneralClasses'");
    }

    @Transactional
    @Override
    public GeneralClass updateGeneralClassSchedule(String semester, UpdateGeneralClassScheduleRequest request) {
        GeneralClass gClassOpened = gcoRepo.findById(Long.parseLong(request.getGeneralClassId())).orElse(null);
        List<GeneralClass> generalClassList = gcoRepo.findAllBySemester(semester);
        RoomReservation rr = roomReservationRepo.findById(request.getRoomReservationId()).orElse(null);
        if (gClassOpened == null || rr == null) throw new NotFoundException("Không tìm thấy lớp hoặc lịch học!");
        if (rr.getStartTime() == null || rr.getEndTime() == null || rr.getWeekday() == null || rr.getRoom() == null) {
            switch (request.getField()) {
                case "startTime":
                    rr.setStartTime(Integer.parseInt(request.getValue()));
                    if (rr.getEndTime() != null && rr.getStartTime() > rr.getEndTime())
                        throw new ConflictScheduleException("Thời gian bắt đầu không thể lớn hơn thời gian kết thúc!");
                    break;
                case "room":
                    rr.setRoom(request.getValue());
                    break;
                case "weekday":
                    rr.setWeekday(request.getValue().equals("Chủ nhật") ? 8 : Integer.parseInt(request.getValue()));
                    break;
                case "endTime":
                    rr.setEndTime(Integer.parseInt(request.getValue()));
                    if (rr.getStartTime() != null && rr.getStartTime() > rr.getEndTime())
                        throw new ConflictScheduleException("Thời gian bắt đầu không thể lớn hơn thời gian kết thúc!");
                    break;
                default:
                    break;
            }
            if (rr.getStartTime() != null && rr.getEndTime() != null && rr.getWeekday() != null && rr.getRoom() != null) {
                if (!ClassTimeComparator.isClassConflict(rr, gClassOpened, generalClassList)) {
                    List<Integer> weeks = LearningWeekExtractor.extractArray(gClassOpened.getLearningWeeks());
                    List<RoomOccupation> roomOccupationList = new ArrayList<>();
                    for (Integer week : weeks) {
                        roomOccupationList.add(new RoomOccupation(
                                rr.getRoom(),
                                gClassOpened.getClassCode(),
                                rr.getStartTime(),
                                rr.getEndTime(),
                                gClassOpened.getCrew(),
                                rr.getWeekday(),
                                week,
                                "study",
                                gClassOpened.getSemester()));
                    }
                    roomOccupationRepo.saveAll(roomOccupationList);
                }
            }
            gcoRepo.save(gClassOpened);
        } else {
            System.out.println(rr);
            List<RoomOccupation> roomOccupationList = roomOccupationRepo.findAllBySemesterAndClassCodeAndDayIndexAndStartPeriodAndEndPeriodAndClassRoom(
                    gClassOpened.getSemester(),
                    gClassOpened.getClassCode(),
                    rr.getWeekday(),
                    rr.getStartTime(),
                    rr.getEndTime(),
                    rr.getRoom());
            switch (request.getField()) {
                case "startTime":
                    rr.setStartTime(Integer.parseInt(request.getValue()));
                    if (rr.getEndTime() != null && rr.getStartTime() > rr.getEndTime())
                        throw new ConflictScheduleException("Thời gian bắt đầu không thể lớn hơn thời gian kết thúc!");
                    if (!ClassTimeComparator.isClassConflict(rr, gClassOpened, generalClassList)) {
                        roomOccupationList.forEach(ro -> {
                            ro.setStartPeriod(rr.getStartTime());
                        });
                    }
                    break;
                case "room":
                    rr.setRoom(request.getValue());
                    if (!ClassTimeComparator.isClassConflict(rr, gClassOpened, generalClassList)) {
                        rr.setRoom(request.getValue());
                        roomOccupationList.forEach(ro -> {
                            ro.setClassRoom(rr.getRoom());
                        });
                    }
                    break;
                case "weekday":
                    rr.setWeekday(request.getValue().equals("Chủ nhật") ? 8 : Integer.parseInt(request.getValue()));
                    if (!ClassTimeComparator.isClassConflict(rr, gClassOpened, generalClassList)) {
                        roomOccupationList.forEach(ro -> {
                            ro.setDayIndex(rr.getWeekday());
                        });
                    }
                    break;
                case "endTime":
                    rr.setEndTime(Integer.parseInt(request.getValue()));
                    if (rr.getStartTime() != null && rr.getStartTime() > rr.getEndTime())
                        throw new ConflictScheduleException("Thời gian bắt đầu không thể lớn hơn thời gian kết thúc!");
                    if (!ClassTimeComparator.isClassConflict(rr, gClassOpened, generalClassList)) {
                        roomOccupationList.forEach(ro -> {
                            ro.setEndPeriod(rr.getEndTime());
                        });
                    }
                    break;
                default:
                    gcoRepo.save(gClassOpened);
                    roomOccupationRepo.saveAll(roomOccupationList);
                    break;
            }
        }
        return gClassOpened;
    }

    @Override
    public GeneralClass updateGeneralClass(UpdateGeneralClassRequest request) {
        GeneralClass gClassOpened = gcoRepo.findById(Long.parseLong(request.getGeneralClassId())).orElse(null);
        switch (request.getField()) {
            case "studyClass":
                gClassOpened.setStudyClass(request.getValue());
                gcoRepo.save(gClassOpened);
                break;
            case "groupName":
                gClassOpened.setGroupName(request.getValue());
                gcoRepo.save(gClassOpened);
        }
        return gClassOpened;
    }

    @Override
    public List<GeneralClass> addClassesToNewGroup(List<String> ids, String groupName, String priorityBuilding) throws Exception {
        if (!groupRepo.getAllByGroupName(groupName).isEmpty()) {
            throw new Exception("Group name has existed!");
        } else {
            groupRepo.save(new Group(null, groupName, priorityBuilding));
        }
        List<GeneralClass> generalClassList = new ArrayList<>();
        for (String id : ids) {
            GeneralClass generalClass = gcoRepo.findById(Long.valueOf(id)).orElse(null);
            if (generalClass == null) {
                System.err.println("Class not exist with id =" + id);
                continue;
            }
            generalClass.setGroupName(groupName);
            generalClassList.add(generalClass);
        }
        gcoRepo.saveAll(generalClassList);
        return gcoRepo.findAll();
    }

    @Override
    public List<GeneralClass> addClassesToCreatedGroup(List<String> ids, String groupName) throws Exception {
        List<GeneralClass> generalClassList = new ArrayList<>();
        for (String id : ids) {
            GeneralClass generalClass = gcoRepo.findById(Long.valueOf(id)).orElse(null);
            if (generalClass == null) {
                System.err.println("Class not exist with id =" + id);
                continue;
            }
            generalClass.setGroupName(groupName);
            generalClassList.add(generalClass);
        }
        gcoRepo.saveAll(generalClassList);
        return gcoRepo.findAll();
    }

    @Transactional
    @Override
    public void deleteClassesBySemester(String semester) {
        gcoRepo.deleteBySemester(semester);
        roomOccupationRepo.deleteBySemester(semester);
    }

    @Transactional
    @Override
    public List<GeneralClass> resetSchedule(List<String> ids, String semester) {
        List<GeneralClass> generalClassList = gcoRepo.findAllBySemester(semester);
        if (generalClassList.isEmpty()) {
            throw new NotFoundException("Không tìm thấy lớp, hãy kiểm tra lại danh sách lớp!");
        } else {
            List<GeneralClass> filteredGeneralClassList = new ArrayList<>();
            for (GeneralClass gClass : generalClassList) {
                for (String idString : ids) {
                    log.info("resetSchedule, consider id " + idString);
                    int gId = Integer.parseInt(idString.split("-")[0]);
                    int timeSlotIndex = Integer.parseInt(idString.split("-")[1]) - 1;
                    if (gId == gClass.getId()) {
                        RoomReservation timeSlot = gClass.getTimeSlots().get(timeSlotIndex);
                        if (timeSlot.getStartTime() != null && timeSlot.getEndTime() != null && timeSlot.getRoom() != null && timeSlot.getWeekday() != null && !timeSlot.getRoom().isEmpty()) {
                            roomOccupationRepo.deleteAllByClassCodeAndStartPeriodAndEndPeriodAndDayIndexAndClassRoom(gClass.getClassCode(), timeSlot.getStartTime(), timeSlot.getEndTime(), timeSlot.getWeekday(), timeSlot.getRoom());
                        }
                        timeSlot.setWeekday(null);
                        timeSlot.setStartTime(null);
                        timeSlot.setEndTime(null);
                        timeSlot.setRoom(null);
                        filteredGeneralClassList.add(gClass);
                    }
                }
            }
            gcoRepo.saveAll(filteredGeneralClassList);
        }
        return generalClassList;
    }

    @Transactional
    @Override
    public List<GeneralClass> autoScheduleRoom(String semester, String groupName, int timeLimit) {
        log.info("autoScheduleRoom start...");
        List<GeneralClass> classes = gcoRepo.findAllBySemesterAndGroupName(semester, groupName);
        if (classes == null) throw new NotFoundException("Không tìm thấy lớp");
        Group group = groupRepo.findByGroupName(groupName).orElse(null);
        if(group == null) throw new NotFoundException("Nhóm không tồn tại!");
        List<Classroom> rooms = classroomRepo
                .getClassRoomByBuildingIn(Arrays.stream(group.getPriorityBuilding().split(",")).toList());
        List<RoomOccupation> roomOccupations = roomOccupationRepo.findAllBySemester(semester);
        List<GeneralClass> updatedClasses = V2ClassScheduler.autoScheduleRoom(classes, rooms, timeLimit, roomOccupations);
        List<String> classCodes = updatedClasses.stream().map(GeneralClass::getClassCode).toList();
        List<RoomOccupation> newRoomOccupations = updatedClasses.stream().map(RoomOccupationMapper::mapFromGeneralClass).flatMap(Collection::stream).toList();
        roomOccupationRepo.deleteAllByClassCodeIn(classCodes);
        roomOccupationRepo.saveAll(newRoomOccupations);
        gcoRepo.saveAll(updatedClasses);
        return updatedClasses;
    }

    @Transactional
    @Override
    public List<GeneralClass> autoSchedule(String semester, String groupName, int timeLimit) {
        log.info("autoSchedule START....");
        List<GeneralClass> foundClasses = gcoRepo.findAllBySemesterAndGroupName(semester, groupName);
        List<GeneralClass> autoScheduleClasses = V2ClassScheduler.autoScheduleTimeSlot(foundClasses, timeLimit);
        /*Save the scheduled timeslot of the classes*/
        gcoRepo.saveAll(autoScheduleClasses);
        roomOccupationRepo.deleteAllByClassCodeIn(foundClasses.stream().map(GeneralClass::getClassCode).toList());
        /*Get the roomOccupations from scheduled classes and save them*/
        List<RoomOccupation> newRoomOccupationList = autoScheduleClasses.stream().map(RoomOccupationMapper::mapFromGeneralClass).flatMap(List::stream).toList();
        roomOccupationRepo.saveAll(newRoomOccupationList);
        return autoScheduleClasses;
    }

    @Override
    public InputStream exportExcel(String semester) {
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
        return excelHelper.convertGeneralClassToExcel(classes);
    }

    @Transactional
    @Override
    public List<GeneralClass> v2UpdateClassSchedule(String semester, List<V2UpdateClassScheduleRequest> request) {
        /*Find reference*/
        List<GeneralClass> classes = gcoRepo.findAllBySemester(semester);
        List<RoomReservation> roomReservations = classes.stream().map(GeneralClass::getTimeSlots).flatMap(Collection::stream).toList();

        HashMap<Long, RoomReservation> roomReservationMap = new HashMap<>();
        List<Long> requestIds = request.stream().map(V2UpdateClassScheduleRequest::getRoomReservationId).toList();
        List<RoomOccupation> roomOccupations = new ArrayList<>();

        roomReservations.forEach(roomReservation -> {
            if (requestIds.contains(roomReservation.getId())) {
                roomReservationMap.put(roomReservation.getId(), roomReservation);
            }
        });
        /*Set info*/
        request.forEach(updateRequest -> {
            RoomReservation updateRoomReservation = roomReservationMap.get(updateRequest.getRoomReservationId());
            if (updateRoomReservation.isTimeSlotNotNull()) {

            }
            List<RoomOccupation> foundRoomOccupations = roomOccupationRepo.findAllBySemesterAndClassCodeAndDayIndexAndStartPeriodAndEndPeriodAndClassRoom(semester,
                    updateRoomReservation.getGeneralClass().getClassCode(),
                    updateRoomReservation.getWeekday(),
                    updateRoomReservation.getStartTime(),
                    updateRoomReservation.getEndTime(),
                    updateRoomReservation.getRoom());


            updateRoomReservation.setStartTime(updateRequest.getStartTime());
            updateRoomReservation.setEndTime(updateRequest.getEndTime());
            updateRoomReservation.setWeekday(updateRequest.getWeekday());
            updateRoomReservation.setRoom(updateRequest.getRoom());


            if (
                /*After schedule is complete*/
                updateRoomReservation.isScheduleNotNull() &&
                /*Before schedule is not complete*/
                foundRoomOccupations.isEmpty()
            ) {
                List<Integer> weeks = LearningWeekExtractor.extractArray(updateRoomReservation.getGeneralClass().getLearningWeeks());
                List<RoomOccupation> roomOccupationList = new ArrayList<>();
                for (Integer week : weeks) {
                    roomOccupationList.add(new RoomOccupation(
                            updateRoomReservation.getRoom(),
                            updateRoomReservation.getGeneralClass().getClassCode(),
                            updateRoomReservation.getStartTime(),
                            updateRoomReservation.getEndTime(),
                            updateRoomReservation.getGeneralClass().getCrew(),
                            updateRoomReservation.getWeekday(),
                            week,
                            "study",
                            semester));
                }
                roomOccupations.addAll(roomOccupationList);
            } else if (
                /*After schedule is complete*/
                updateRoomReservation.isScheduleNotNull() &&
                /*Before schedule is complete*/
                !foundRoomOccupations.isEmpty()
            ) {
                foundRoomOccupations.forEach(roomOccupation -> {
                    roomOccupation.setClassRoom(updateRequest.getRoom());
                    roomOccupation.setStartPeriod(updateRequest.getStartTime());
                    roomOccupation.setEndPeriod(updateRequest.getEndTime());
                    roomOccupation.setDayIndex(updateRequest.getWeekday());
                });
            } else {
                /*After the schedule is not complete*/
                /*Do nothing */
            }
            roomOccupations.addAll(foundRoomOccupations);
        });
        /*Check conflict*/
        for (RoomReservation roomReservation : roomReservationMap.values()) {
            if (roomReservation.isScheduleNotNull()) {
                ClassTimeComparator.isClassConflict(roomReservation, roomReservation.getGeneralClass(), classes);
            }
        }
        /* Persist room reservation and room occupation */
        roomReservationRepo.saveAll(roomReservationMap.values());
        roomOccupationRepo.saveAll(roomOccupations);
        return roomReservationMap.values().stream().map(RoomReservation::getGeneralClass).toList();

    }
}