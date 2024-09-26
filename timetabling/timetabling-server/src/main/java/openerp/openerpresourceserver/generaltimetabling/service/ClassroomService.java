package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.ClassroomDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Classroom;

import java.util.List;

public interface ClassroomService {

    List<Classroom> getClassroom();

    List<String> getBuilding();

    void updateClassroom(ClassroomDto requestDto);

    Classroom create(ClassroomDto classroomDto);

    void deleteById(Long id);

    void deleteByIds(List<Long> ids);

    void clearAllClassRoom();

    void clearAllClassRoomTimetable();

    List<Classroom> getMaxQuantityClassRoomByBuildings(String groupName, int maxAmount);
}
