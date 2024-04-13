package openerp.openerpresourceserver.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.micrometer.common.lang.NonNull;
import openerp.openerpresourceserver.model.entity.occupation.RoomOccupation;
import openerp.openerpresourceserver.repo.RoomOccupationRepo;
import openerp.openerpresourceserver.service.RoomOccupationService;

@Service
public class RoomOccupationSeriviceImp implements RoomOccupationService {

    @Autowired
    private RoomOccupationRepo roomOccupationRepo;

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

}
