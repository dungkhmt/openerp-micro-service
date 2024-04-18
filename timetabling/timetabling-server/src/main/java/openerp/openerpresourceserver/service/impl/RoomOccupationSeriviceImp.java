package openerp.openerpresourceserver.service.impl;

import java.io.ByteArrayInputStream;
import java.util.List;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.helper.GeneralExcelHelper;
import openerp.openerpresourceserver.repo.AcademicWeekRepo;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.model.entity.occupation.RoomOccupation;
import openerp.openerpresourceserver.repo.RoomOccupationRepo;
import openerp.openerpresourceserver.service.RoomOccupationService;

@Service
@AllArgsConstructor
public class RoomOccupationSeriviceImp implements RoomOccupationService {

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
