package openerp.openerpresourceserver.generaltimetabling.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.RoomOccupation;


public interface RoomOccupationService {
     List<RoomOccupation> getRoomOccupationsBySemester(String semester);
    
     void saveRoomOccupation(RoomOccupation r);

     void saveAll(List<RoomOccupation> roomOccupations);

     ByteArrayInputStream exportExcel(String semester, List<Integer> weeks);
}
