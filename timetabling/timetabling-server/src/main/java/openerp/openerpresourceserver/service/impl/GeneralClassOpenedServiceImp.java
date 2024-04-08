package openerp.openerpresourceserver.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import openerp.openerpresourceserver.model.dto.request.general.UpdateClassesToNewGroupRequest;
import openerp.openerpresourceserver.model.entity.Group;
import openerp.openerpresourceserver.repo.GroupRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.model.dto.request.general.UpdateGeneralClassRequest;
import openerp.openerpresourceserver.model.dto.request.general.UpdateGeneralClassScheduleRequest;
import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.repo.GeneralClassOpenedRepository;
import openerp.openerpresourceserver.service.GeneralClassOpenedService;

/**
 * GeneralClassOpenedServiceImp
 */
@Service
public class GeneralClassOpenedServiceImp implements GeneralClassOpenedService {

    @Autowired
    private GeneralClassOpenedRepository gcoRepo;

    @Autowired
    private GroupRepo groupRepo;

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

    @Override
    public GeneralClassOpened updateGeneralClassSchedule(UpdateGeneralClassScheduleRequest request) {
        GeneralClassOpened gClassOpened = gcoRepo.findById(Long.parseLong(request.getGeneralClassId())).orElse(null);
        switch (request.getField()) {
            case "startTime":
                if (/* Add to check conflict here */ false)
                    throw new RuntimeException("Invalid schedule time!");
                int startTime = Integer.parseInt(request.getValue());
                int endTime = startTime +
                        (gClassOpened.getTimeSlots().get(Integer.parseInt(request.getScheduleIndex()) - 1).getEndTime()
                                - gClassOpened.getTimeSlots().get(Integer.parseInt(request.getScheduleIndex()) - 1)
                                        .getStartTime());
                gClassOpened.getTimeSlots().get(Integer.parseInt(request.getScheduleIndex()) - 1)
                        .setStartTime(startTime);
                gClassOpened.getTimeSlots().get(Integer.parseInt(request.getScheduleIndex()) - 1)
                        .setEndTime(endTime);
                gcoRepo.save(gClassOpened);
                break;
            case "room":
                if (/* Check conflitct room here */ false)
                    throw new RuntimeException("Invalid schedule time!");
                gClassOpened.getTimeSlots().get(Integer.parseInt(request.getScheduleIndex())-1)
                        .setRoom(request.getValue());
                gcoRepo.save(gClassOpened);
                break;
            case "weekday":
                if (/* Check conflitct room here */ false)
                    throw new RuntimeException("Invalid schedule time!");
                gClassOpened.getTimeSlots().get(Integer.parseInt(request.getScheduleIndex())-1)
                        .setWeekday(Integer.parseInt(request.getValue()));
                gcoRepo.save(gClassOpened);
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
}