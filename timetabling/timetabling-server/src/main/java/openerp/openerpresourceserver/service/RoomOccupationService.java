package openerp.openerpresourceserver.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import openerp.openerpresourceserver.model.entity.occupation.RoomOccupation;

public interface RoomOccupationService {
    public List<RoomOccupation> getRoomOccupationsBySemester(String semester);
    
    public void saveRoomOccupation(RoomOccupation r);

    public void saveAll(List<RoomOccupation> roomOccupations);

    public ByteArrayInputStream exportExcel(String semester, List<Integer> weeks);
}
