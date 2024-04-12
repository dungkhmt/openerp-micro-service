package openerp.openerpresourceserver.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.helper.ClassTimeComparator;
import openerp.openerpresourceserver.model.entity.Group;
import openerp.openerpresourceserver.model.entity.general.RoomReservation;
import openerp.openerpresourceserver.model.entity.occupation.RoomOccupation;
import openerp.openerpresourceserver.repo.GroupRepo;
import openerp.openerpresourceserver.repo.RoomOccupationRepo;
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
@Service
@AllArgsConstructor
public class GeneralClassOpenedServiceImp implements GeneralClassOpenedService {

    private GeneralClassOpenedRepository gcoRepo;

    private GroupRepo groupRepo;

    private RoomOccupationRepo roomOccupationRepo;


    @Override
    public List<GeneralClassOpened> getGeneralClasses(String semester) {
        List<GeneralClassOpened> generalClassOpenedList = gcoRepo.findAll();
        return generalClassOpenedList.stream()
                .filter(gClass -> gClass.getSemester().equals(semester))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllGeneralClasses() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteAllGeneralClasses'");
    }

    @Transactional
    @Override
    public GeneralClassOpened updateGeneralClassSchedule(UpdateGeneralClassScheduleRequest request) {
        GeneralClassOpened gClassOpened = gcoRepo.findById(Long.parseLong(request.getGeneralClassId())).orElse(null);
        List<GeneralClassOpened> generalClassOpenedList = gcoRepo.findAll();
        RoomReservation rr = gClassOpened.getTimeSlots().get(Integer.parseInt(request.getScheduleIndex())-1);
        int startPeriod = rr.getStartTime();
        int endPeriod = rr.getEndTime();
        int weekDay = rr.getWeekday();
        String classRoom = rr.getRoom();
        List<RoomOccupation> roomOccupationList = roomOccupationRepo.findAllBySemesterAndClassCodeAndDayIndexAndStartPeriodAndEndPeriodAndClassRoom(gClassOpened.getSemester(), gClassOpened.getClassCode(), weekDay, startPeriod, endPeriod, classRoom);

        switch (request.getField()) {
            case "startTime":
                int duration = gClassOpened.getTimeSlots().get(Integer.parseInt(request.getScheduleIndex())-1).getEndTime() -
                        gClassOpened.getTimeSlots().get(Integer.parseInt(request.getScheduleIndex())-1).getStartTime();
                gClassOpened.getTimeSlots().get(Integer.parseInt(request.getScheduleIndex())-1).setStartTime(Integer.parseInt(request.getValue()));
                gClassOpened.getTimeSlots().get(Integer.parseInt(request.getScheduleIndex())-1).setEndTime(Integer.parseInt(request.getValue())+duration);
                if (!ClassTimeComparator.isConflict(Integer.parseInt(request.getScheduleIndex())-1,gClassOpened, generalClassOpenedList)) {
                    roomOccupationList.forEach(ro->{
                        ro.setStartPeriod(gClassOpened.getTimeSlots().get(Integer.parseInt(request.getScheduleIndex())-1).getStartTime());
                        ro.setEndPeriod(gClassOpened.getTimeSlots().get(Integer.parseInt(request.getScheduleIndex())-1).getEndTime());
                    });
                    gcoRepo.save(gClassOpened);
                    roomOccupationRepo.saveAll(roomOccupationList);
                }
                break;
            case "room":
                if (!ClassTimeComparator.isConflict(Integer.parseInt(request.getScheduleIndex())-1,gClassOpened, generalClassOpenedList)) {
                    gClassOpened.getTimeSlots().get(Integer.parseInt(request.getScheduleIndex())-1).setRoom(request.getValue());
                    roomOccupationList.forEach(ro->{
                        ro.setClassRoom(gClassOpened.getTimeSlots().get(Integer.parseInt(request.getScheduleIndex())-1).getRoom());
                    });
                    gcoRepo.save(gClassOpened);
                    roomOccupationRepo.saveAll(roomOccupationList);
                }
                break;
            case "weekday":
                gClassOpened.getTimeSlots().get(Integer.parseInt(request.getScheduleIndex())-1).setWeekday(Integer.parseInt(request.getValue()));
                if (!ClassTimeComparator.isConflict(Integer.parseInt(request.getScheduleIndex())-1,gClassOpened, generalClassOpenedList)) {
                    roomOccupationList.forEach(ro->{
                        ro.setDayIndex(gClassOpened.getTimeSlots().get(Integer.parseInt(request.getScheduleIndex())-1).getWeekday());
                    });
                    gcoRepo.save(gClassOpened);
                    roomOccupationRepo.saveAll(roomOccupationList);
                }
                break;
            default:
                break;
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
}