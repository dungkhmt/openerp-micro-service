package openerp.openerpresourceserver.generaltimetabling.service.impl;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Classroom;
import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.RoomOccupation;
import openerp.openerpresourceserver.generaltimetabling.repo.AcademicWeekRepo;
import openerp.openerpresourceserver.generaltimetabling.repo.ClassroomRepo;
import openerp.openerpresourceserver.generaltimetabling.repo.RoomOccupationRepo;
import openerp.openerpresourceserver.generaltimetabling.service.RoomOccupationService;
import openerp.openerpresourceserver.generaltimetabling.helper.GeneralExcelHelper;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RoomOccupationServiceImp implements RoomOccupationService {

    private RoomOccupationRepo roomOccupationRepo;
    private AcademicWeekRepo academicWeekRepo;
    private GeneralExcelHelper excelHelper;
    private ClassroomRepo classroomRepo;
    @Override
    public List<RoomOccupation> getRoomOccupationsBySemester(String semester) {
        return roomOccupationRepo.findAllBySemester(semester);
    }

    @Override
    public void saveRoomOccupation(RoomOccupation room) {
        roomOccupationRepo.save(room);
    }

    @Override
    public void saveAll(List<RoomOccupation> roomOccupations) {
        roomOccupationRepo.saveAll(roomOccupations);
    }

    @Override
    public ByteArrayInputStream exportExcel(String semester, int week) {
        return excelHelper.convertRoomOccupationToExcel(roomOccupationRepo.findAllBySemesterAndWeekIndex(semester, week));
    }

    @Override
    public List<RoomOccupation> getRoomOccupationsBySemesterAndWeekIndex(String semester, int weekIndex) {
        return roomOccupationRepo.findAllBySemesterAndWeekIndex(semester,weekIndex);
    }

    @Override
    public List<Classroom> getRoomsNotOccupiedBySemesterAndWeekDayCrewStartAndEndSLot(String semester, String crew, int week, int day, int startSlot, int endSlot) {
        // collect all rooms occupied in the given semester, crew (morning (S)/afternoon(C)), week, day
        List<RoomOccupation> lst = roomOccupationRepo.
                findAllBySemesterAndCrewAndWeekIndexAndDayIndex(semester,crew,week,day);
        List<Classroom> rooms = classroomRepo.findAll();
        HashSet<String> occupiedRooms = new HashSet();
        for(RoomOccupation ro: lst){
            boolean notOverlap = ro.getStartPeriod() > endSlot ||
                    startSlot > ro.getEndPeriod();
            if(notOverlap == false){
                occupiedRooms.add(ro.getClassRoom());
            }
        }
        List<Classroom> res = new ArrayList<>();
        for(Classroom r: rooms){
            if(!occupiedRooms.contains(r.getClassroom()))
                res.add(r);
        }
        return res;

    }



}

