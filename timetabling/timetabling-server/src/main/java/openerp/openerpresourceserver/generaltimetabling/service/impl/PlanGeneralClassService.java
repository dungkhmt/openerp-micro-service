package openerp.openerpresourceserver.generaltimetabling.service.impl;


import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.generaltimetabling.model.dto.MakeGeneralClassRequest;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.PlanGeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.RoomReservation;
import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.RoomOccupation;
import openerp.openerpresourceserver.generaltimetabling.repo.GeneralClassRepository;
import openerp.openerpresourceserver.generaltimetabling.repo.PlanGeneralClassRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Service
public class PlanGeneralClassService {
    private GeneralClassRepository generalClassRepository;
    private PlanGeneralClassRepository planGeneralClassRepository;
    public GeneralClass makeClass(MakeGeneralClassRequest request) {
        GeneralClass newClass = new GeneralClass();

        newClass.setRefClassId(request.getId());
        newClass.setSemester(request.getSemester());
        newClass.setModuleCode(request.getModuleCode());
        newClass.setModuleName(request.getModuleName());
        newClass.setMass(request.getMass());
        List<RoomReservation> roomReservations = new ArrayList<>();
        RoomReservation roomReservation =  new RoomReservation();
        roomReservation.setGeneralClass(newClass);
        roomReservations.add(roomReservation);

        newClass.setTimeSlots(roomReservations);

        return generalClassRepository.save(newClass);
    }

    public List<PlanGeneralClass> getAllClasses(String semester) {
        return planGeneralClassRepository.findAllBySemester(semester);
    }

    @Transactional
    public void deleteAllClasses(String semester) {
        planGeneralClassRepository.deleteAllBySemester(semester);
    }

    public List<GeneralClass> getPlanClassById(String semester, Long planClassId) {
        return generalClassRepository.findClassesByRefClassIdAndSemester(planClassId, semester);
    }

    public GeneralClass updateGeneralClass(GeneralClass generalClass) {
        GeneralClass updateGeneralClass = generalClassRepository.findById(generalClass.getId()).orElse(null);
        updateGeneralClass.setParentClassId(generalClass.getParentClassId());
        updateGeneralClass.setQuantityMax(generalClass.getQuantityMax());
        updateGeneralClass.setClassType(generalClass.getClassType());
        return generalClassRepository.save(updateGeneralClass);
    }
}
