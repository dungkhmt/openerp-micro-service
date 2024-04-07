package openerp.openerpresourceserver.service.impl;

import java.lang.reflect.Type;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import openerp.openerpresourceserver.model.entity.general.RoomReservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.controller.general.GeneralClassOpenedController;
import openerp.openerpresourceserver.helper.MassExtractor;
import openerp.openerpresourceserver.model.dto.request.UpdateGeneralClassRequest;
import openerp.openerpresourceserver.model.dto.request.UpdateGeneralClassScheduleRequest;
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
        }
        return gClassOpened;
    }

}