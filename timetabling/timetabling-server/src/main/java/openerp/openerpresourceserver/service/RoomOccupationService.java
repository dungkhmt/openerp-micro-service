package openerp.openerpresourceserver.service;

import java.util.List;

import openerp.openerpresourceserver.model.entity.occupation.RoomOccupation;

public interface RoomOccupationService {
    public List<RoomOccupation> getRoomOccupationsBySemester(String semester);
    
    public void saveRoomOccupation(RoomOccupation r);

    public void saveAll(List<RoomOccupation> roomOccupations);
}
