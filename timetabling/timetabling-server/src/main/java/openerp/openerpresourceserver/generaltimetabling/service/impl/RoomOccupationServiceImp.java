package openerp.openerpresourceserver.generaltimetabling.service.impl;

import java.io.ByteArrayInputStream;
import java.util.List;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.RoomOccupation;
import openerp.openerpresourceserver.generaltimetabling.repo.AcademicWeekRepo;
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
    public ByteArrayInputStream exportExcel(String semester, List<Integer> weeks) {
        int maxWeek = 1;
        for(int week : weeks) {
            if(week >= maxWeek) {
                maxWeek = week;
            }
        }
        return excelHelper.convertToExcel(roomOccupationRepo.findAllBySemesterAndWeekIndex(semester, maxWeek), maxWeek);
    }

}

