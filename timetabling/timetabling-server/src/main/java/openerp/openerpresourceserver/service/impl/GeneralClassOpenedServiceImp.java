package openerp.openerpresourceserver.service.impl;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.exception.ConflictScheduleException;
import openerp.openerpresourceserver.exception.NotFoundException;
import openerp.openerpresourceserver.helper.ClassTimeComparator;
import openerp.openerpresourceserver.helper.LearningWeekExtractor;
import openerp.openerpresourceserver.model.entity.Group;
import openerp.openerpresourceserver.model.entity.general.RoomReservation;
import openerp.openerpresourceserver.model.entity.occupation.RoomOccupation;
import openerp.openerpresourceserver.repo.GroupRepo;
import openerp.openerpresourceserver.repo.RoomOccupationRepo;
import openerp.openerpresourceserver.repo.RoomReservationRepo;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.model.dto.request.general.UpdateGeneralClassRequest;
import openerp.openerpresourceserver.model.dto.request.general.UpdateGeneralClassScheduleRequest;
import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.repo.GeneralClassOpenedRepository;
import openerp.openerpresourceserver.service.GeneralClassOpenedService;
import org.springframework.transaction.annotation.Transactional;

/**
 * GeneralClassOpenedServiceImp
 */
@AllArgsConstructor
@Service
public class GeneralClassOpenedServiceImp implements GeneralClassOpenedService {

    private GeneralClassOpenedRepository gcoRepo;

    private GroupRepo groupRepo;

    private RoomOccupationRepo roomOccupationRepo;

    private RoomReservationRepo roomReservationRepo;

    @Override
    public List<GeneralClassOpened> getGeneralClasses(String semester) {
        return gcoRepo.findAllBySemester(semester);
    }

    @Override
    public void deleteAllGeneralClasses() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAllGeneralClasses'");
    }

    @Transactional
    @Override
    public GeneralClassOpened updateGeneralClassSchedule(String semester, UpdateGeneralClassScheduleRequest request) {
        GeneralClassOpened gClassOpened = gcoRepo.findById(Long.parseLong(request.getGeneralClassId())).orElse(null);
        List<GeneralClassOpened> generalClassOpenedList = gcoRepo.findAllBySemester(semester);
        RoomReservation rr = roomReservationRepo.findById(request.getRoomReservationId()).orElse(null);
        if (gClassOpened == null || rr == null) throw new NotFoundException("Không tìm thấy lớp hoặc lịch học!");
        if (rr.getStartTime() == null || rr.getEndTime() == null || rr.getWeekday() == null || rr.getRoom() == null) {
            switch (request.getField()) {
                case "startTime":
                    rr.setStartTime(Integer.parseInt(request.getValue()));
                    if (rr.getEndTime() != null && rr.getStartTime() >= rr.getEndTime()) throw new ConflictScheduleException("Thời gian bắt đầu không thể lớn hơn hoặc bằng với thời gian kết thúc!");
                    break;
                case "room":
                    rr.setRoom(request.getValue());
                    break;
                case "weekday":
                    rr.setWeekday(request.getValue().equals("Chủ nhật") ? 8 : Integer.parseInt(request.getValue()));
                    break;
                case "endTime":
                    rr.setEndTime(Integer.parseInt(request.getValue()));
                    if (rr.getStartTime() != null && rr.getStartTime() >= rr.getEndTime()) throw new ConflictScheduleException("Thời gian bắt đầu không thể lớn hơn hoặc bằng với thời gian kết thúc!");
                    break;
                default:
                    break;
            }
            if (rr.getStartTime() != null && rr.getEndTime() != null && rr.getWeekday() != null && rr.getRoom() != null) {
                if (!ClassTimeComparator.isClassConflict(rr, gClassOpened, generalClassOpenedList)) {
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
                    if (rr.getEndTime() != null && rr.getStartTime() >= rr.getEndTime()) throw new ConflictScheduleException("Thời gian bắt đầu không thể lớn hơn hoặc bằng với thời gian kết thúc!");
                    if (!ClassTimeComparator.isClassConflict(rr, gClassOpened, generalClassOpenedList)) {
                        roomOccupationList.forEach(ro -> {
                            ro.setStartPeriod(rr.getStartTime());
                        });
                    }
                    break;
                case "room":
                    rr.setRoom(request.getValue());
                    if (!ClassTimeComparator.isClassConflict(rr, gClassOpened, generalClassOpenedList)) {
                        rr.setRoom(request.getValue());
                        roomOccupationList.forEach(ro -> {
                            ro.setClassRoom(rr.getRoom());
                        });
                    }
                    break;
                case "weekday":
                    rr.setWeekday(request.getValue().equals("Chủ nhật") ? 8 : Integer.parseInt(request.getValue()));
                    if (!ClassTimeComparator.isClassConflict(rr, gClassOpened, generalClassOpenedList)) {
                        roomOccupationList.forEach(ro -> {
                            ro.setDayIndex(rr.getWeekday());
                        });
                    }
                    break;
                case "endTime":
                    rr.setEndTime(Integer.parseInt(request.getValue()));
                    if (rr.getStartTime() != null && rr.getStartTime() >= rr.getEndTime()) throw new ConflictScheduleException("Thời gian bắt đầu không thể lớn hơn hoặc bằng với thời gian kết thúc!");
                    if (!ClassTimeComparator.isClassConflict(rr, gClassOpened, generalClassOpenedList)) {
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
    public GeneralClassOpened updateGeneralClass(UpdateGeneralClassRequest request) {
        GeneralClassOpened gClassOpened = gcoRepo.findById(Long.parseLong(request.getGeneralClassId())).orElse(null);
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
    public List<GeneralClassOpened> addClassesToNewGroup(List<String> ids, String groupName, String priorityBuilding) throws Exception {
        if(!groupRepo.getAllByGroupName(groupName).isEmpty()) {
            throw new Exception("Group name has existed!");
        } else {
            groupRepo.save(new Group(null, groupName, priorityBuilding));
        }
        List<GeneralClassOpened> generalClassOpenedList = new ArrayList<>();
        for (String id : ids) {
            GeneralClassOpened generalClassOpened = gcoRepo.findById(Long.valueOf(id)).orElse(null);
            if (generalClassOpened == null) {
                System.err.println("Class not exist with id =" + id);
                continue;
            }
            generalClassOpened.setGroupName(groupName);
            generalClassOpenedList.add(generalClassOpened);
        }
        gcoRepo.saveAll(generalClassOpenedList);
        return gcoRepo.findAll();
    }

    @Override
    public List<GeneralClassOpened> addClassesToCreatedGroup(List<String> ids, String groupName) throws Exception {
        List<GeneralClassOpened> generalClassOpenedList = new ArrayList<>();
        for (String id : ids) {
            GeneralClassOpened generalClassOpened = gcoRepo.findById(Long.valueOf(id)).orElse(null);
            if (generalClassOpened == null) {
                System.err.println("Class not exist with id =" + id);
                continue;
            }
            generalClassOpened.setGroupName(groupName);
            generalClassOpenedList.add(generalClassOpened);
        }
        gcoRepo.saveAll(generalClassOpenedList);
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
    public List<GeneralClassOpened> resetSchedule(List<String> ids, String semester) {
        List<GeneralClassOpened> generalClassOpenedList = gcoRepo.findAllBySemester(semester);
        if(generalClassOpenedList.size() == 0) {
            throw new NotFoundException("Không tìm thấy lớp, hãy kiểm tra lại danh sách lớp!");
        } else {
            List<GeneralClassOpened>filteredGeneralClassList = new ArrayList<>();
            for (GeneralClassOpened gClass : generalClassOpenedList) {
                for (String idString : ids) {
                    int gId = Integer.parseInt(idString.split("-")[0]);
                    int timeSlotIndex = Integer.parseInt(idString.split("-")[1])-1;
                    if(gId == gClass.getId()) {
                        RoomReservation timeSlot = gClass.getTimeSlots().get(timeSlotIndex);
                        if (timeSlot.getStartTime() != null && timeSlot.getEndTime() != null && timeSlot.getRoom() != null && !timeSlot.getRoom().equals("")) {
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
        return generalClassOpenedList;
    }
}