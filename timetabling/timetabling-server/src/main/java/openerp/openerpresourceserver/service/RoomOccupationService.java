package openerp.openerpresourceserver.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import openerp.openerpresourceserver.model.entity.occupation.RoomOccupation;
import org.springframework.stereotype.Service;


public interface RoomOccupationService {
     List<RoomOccupation> getRoomOccupationsBySemester(String semester);
    
     void saveRoomOccupation(RoomOccupation r);

     void saveAll(List<RoomOccupation> roomOccupations);

     ByteArrayInputStream exportExcel(String semester, List<Integer> weeks);
}
