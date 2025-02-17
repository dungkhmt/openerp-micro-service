package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.ClassroomDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Classroom;

import java.util.List;

public interface ClassroomService {

    List<Classroom> getClassroom();

    List<String> getBuilding();

    void updateClassroom(ClassroomDto requestDto);

    Classroom create(ClassroomDto classroomDto);

    void deleteById(String id);

    void deleteByIds(List<String> ids);

    void clearAllClassRoom();

    void clearAllClassRoomTimetable();

    List<Classroom> getMaxQuantityClassRoomByBuildings(String groupName, Integer maxAmount);
}
